package com.hoffi.mppcli.lib.common.io.fs

import okio.FileMetadata
import okio.FileNotFoundException
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

expect val fs: FileSystem

expect fun FileSystem.cwd(): Path

enum class DIRUPSEARCH { WHOLEPATH, PARENT_DIR }
enum class DIRUPSEARCH_MULTIPLE { AND, OR}

fun dirUpsearch(
    fromAbsPathOrRelToCwd: Path = fs.cwd(),
    vararg toSearchFor: String,
    whatToReturn: DIRUPSEARCH = DIRUPSEARCH.WHOLEPATH,
    cutPrefix: Path? = null,
    toSearchType: FileMetadata = FileMetadata(isDirectory = true, isRegularFile = true, symlinkTarget = "/".toPath())
): Path {
    if (fromAbsPathOrRelToCwd.isRoot) { throw Exception("cannot dirUpsearch from root dir") }
    if(toSearchFor.isEmpty()) { throw Exception("toSearchFor is empty, nothing to search for") }
    var fromPath = if (fromAbsPathOrRelToCwd.isAbsolute) fromAbsPathOrRelToCwd else fs.cwd().resolve(fromAbsPathOrRelToCwd, normalize = true)
    // POSIX okio fs.exists(path) crashes if path does not denote a folder, but a regular file
    val fromPathMetadata = fs.metadataOrNull(fromPath)
    if (fromPathMetadata != null && fromPathMetadata.isRegularFile) fromPath = fromPath.parent!!
    var currentPath: Path? = fromPath
    while (currentPath != null) {
        for (candidate in toSearchFor) {
            val pathToFind = currentPath /candidate
            if (fs.exists(pathToFind)) {
                val pathToFindMeta = fs.metadata(pathToFind)
                if ((pathToFindMeta.isDirectory && toSearchType.isDirectory) || (pathToFindMeta.isRegularFile && toSearchType.isRegularFile) || (pathToFindMeta.symlinkTarget != null && toSearchType.symlinkTarget != null)) {
                    val resultPath = when (whatToReturn) {
                        DIRUPSEARCH.WHOLEPATH -> pathToFind
                        DIRUPSEARCH.PARENT_DIR -> currentPath
                    }
                    return if (cutPrefix == null) {
                        resultPath
                    } else {
                        resultPath.toString().removePrefix("$cutPrefix/").toPath()
                    }
                }
            }
        }
        currentPath = currentPath.parent // or null if there is no parent
    }
    throw FileNotFoundException("'${toSearchFor.joinToString("', '", "'", "'")} not found above $fromPath for $toSearchType")
}
