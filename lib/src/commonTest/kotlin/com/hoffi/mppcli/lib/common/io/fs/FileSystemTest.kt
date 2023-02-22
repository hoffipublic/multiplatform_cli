package com.hoffi.mppcli.lib.common.io.fs

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import okio.FileMetadata
import okio.FileNotFoundException
import okio.Path
import okio.Path.Companion.toPath

class FileSystemTest : FunSpec({
    val relFromDirPath = "src/commonMain/kotlin/com/hoffi/mppcli/lib/common/io/fs/".toPath()
    val relFromFilePath = relFromDirPath/"build.gradle.kts"
    val toSearchFor = "build.gradle.kts"
    val toSearchForArr1 = arrayOf(toSearchFor)
    val toSearchForArr2 = arrayOf("something", toSearchFor)
    val toSearchForArr3 = arrayOf("something", "src", toSearchFor)
    val cutPrefix       = (fs.cwd()/"..").normalized()

    val onlyFiles = FileMetadata(isRegularFile = true, isDirectory = false, symlinkTarget = null)
    val onlyDirs  = FileMetadata(isRegularFile = false, isDirectory = true, symlinkTarget = null)

    val expectedAbsPathFile = (fs.cwd()/".."/"lib/$toSearchFor").normalized()
    val expectedAbsPathDir = expectedAbsPathFile.parent!!
    val expectedCutPathFile = expectedAbsPathFile.toString().removePrefix("$cutPrefix/").toPath()
    val expectedCutPathDir = expectedCutPathFile.parent!!

    context("dirUpsearch() for a file") {
        data class D(val fromPath: Path, val toSearchFor: Array<out String>, val whatToReturn: DIRUPSEARCH, val cutPrefix: Path?, val expected: Path)
        withData(mapOf(
            //nameFn = { "${it.a}__${it.b}__${it.c}" },
            "dir,single,wholepath,absolut <$expectedAbsPathFile>" to D(relFromDirPath, toSearchForArr1, DIRUPSEARCH.WHOLEPATH, null, expectedAbsPathFile),
            "dir,single,wholepath,cut: <$expectedCutPathFile>"     to D(relFromDirPath, toSearchForArr1, DIRUPSEARCH.WHOLEPATH, cutPrefix, expectedCutPathFile),
            "dir,single,parent,absolut: <$expectedAbsPathDir>"    to D(relFromDirPath, toSearchForArr1, DIRUPSEARCH.PARENT_DIR, null, expectedAbsPathDir),
            "dir,single,parent,cut: <$expectedCutPathDir>"        to D(relFromDirPath, toSearchForArr1, DIRUPSEARCH.PARENT_DIR, cutPrefix, expectedCutPathDir),

            "dir,multor,wholepath,absolut <$expectedAbsPathFile>" to D(relFromDirPath, toSearchForArr2, DIRUPSEARCH.WHOLEPATH, null, expectedAbsPathFile),
            "dir,multor,wholepath,cut: <$expectedCutPathFile>"     to D(relFromDirPath, toSearchForArr2, DIRUPSEARCH.WHOLEPATH, cutPrefix, expectedCutPathFile),
            "dir,multor,parent,absolut: <$expectedAbsPathDir>"    to D(relFromDirPath, toSearchForArr2, DIRUPSEARCH.PARENT_DIR, null, expectedAbsPathDir),
            "dir,multor,parent,cut: <$expectedCutPathDir>"        to D(relFromDirPath, toSearchForArr2, DIRUPSEARCH.PARENT_DIR, cutPrefix, expectedCutPathDir),
            "dir,mulsrc,wholepath,absolut <$expectedAbsPathFile>" to D(relFromDirPath, toSearchForArr3, DIRUPSEARCH.WHOLEPATH, null, expectedAbsPathDir/"src"),
            "dir,mulsrc,wholepath,cut: <$expectedCutPathFile>"     to D(relFromDirPath, toSearchForArr3, DIRUPSEARCH.WHOLEPATH, cutPrefix, expectedCutPathDir/"src"),
            "dir,mulsrc,parent,absolut: <$expectedAbsPathDir>"    to D(relFromDirPath, toSearchForArr3, DIRUPSEARCH.PARENT_DIR, null, expectedAbsPathDir),
            "dir,mulsrc,parent,cut: <$expectedCutPathDir>"        to D(relFromDirPath, toSearchForArr3, DIRUPSEARCH.PARENT_DIR, cutPrefix, expectedCutPathDir),

            "file,single,wholepath,absolut <$expectedAbsPathFile>" to D(relFromFilePath, toSearchForArr1, DIRUPSEARCH.WHOLEPATH, null, expectedAbsPathFile),
            "file,single,wholepath,cut: <$expectedCutPathFile>"     to D(relFromFilePath, toSearchForArr1, DIRUPSEARCH.WHOLEPATH, cutPrefix, expectedCutPathFile),
            "file,single,parent,absolut: <$expectedAbsPathDir>"    to D(relFromFilePath, toSearchForArr1, DIRUPSEARCH.PARENT_DIR, null, expectedAbsPathDir),
            "file,single,parent,cut: <$expectedCutPathDir>"        to D(relFromFilePath, toSearchForArr1, DIRUPSEARCH.PARENT_DIR, cutPrefix, expectedCutPathDir),

            "file,multor,wholepath,absolut <$expectedAbsPathFile>" to D(relFromFilePath, toSearchForArr2, DIRUPSEARCH.WHOLEPATH, null, expectedAbsPathFile),
            "file,multor,wholepath,cut: <$expectedCutPathFile>"     to D(relFromFilePath, toSearchForArr2, DIRUPSEARCH.WHOLEPATH, cutPrefix, expectedCutPathFile),
            "file,multor,parent,absolut: <$expectedAbsPathDir>"    to D(relFromFilePath, toSearchForArr2, DIRUPSEARCH.PARENT_DIR, null, expectedAbsPathDir),
            "file,multor,parent,cut: <$expectedCutPathDir>"        to D(relFromFilePath, toSearchForArr2, DIRUPSEARCH.PARENT_DIR, cutPrefix, expectedCutPathDir),
        )) { (fromPath, toSearchFor, whatToReturn, cutPrefix, expectedResult) ->
            dirUpsearch(fromPath, *toSearchFor, whatToReturn = whatToReturn, cutPrefix = cutPrefix) shouldBe expectedResult
        }
    }
    context("corner cases") {
        test("fromPath root") {
            shouldThrow<Exception> {
                dirUpsearch("/".toPath(), toSearchFor)
            }
        }
        test("no toSearchFor") {
            shouldThrow<Exception> {
                dirUpsearch(relFromDirPath, *arrayOf<String>())
            }
        }
        test("not found anywhere") {
            shouldThrow<FileNotFoundException> {
                dirUpsearch(relFromDirPath, "Schnederpelz") shouldBe "/something".toPath()
            }
        }
    }
    context("filetype cases") {
        test("only files") {
            dirUpsearch(relFromFilePath, *toSearchForArr3, whatToReturn = DIRUPSEARCH.WHOLEPATH, cutPrefix = cutPrefix, toSearchType = onlyFiles) shouldBe expectedCutPathFile
        }
        test("only dirs") {
            dirUpsearch(relFromFilePath, *toSearchForArr3, whatToReturn = DIRUPSEARCH.WHOLEPATH, cutPrefix = cutPrefix, toSearchType = onlyDirs)  shouldBe expectedCutPathDir/"src"
        }
    }
    context("searchFor is a directory") {
        test("search for src/ dir WHOLEPATH expect ${expectedCutPathDir/"src"}") {
            dirUpsearch(relFromDirPath, "src", whatToReturn = DIRUPSEARCH.WHOLEPATH, cutPrefix = cutPrefix) shouldBe expectedCutPathDir/"src"
        }
        test("search for /src/ dir PARENT_DIR expect ${expectedCutPathDir}") {
            dirUpsearch(relFromDirPath, "src", whatToReturn = DIRUPSEARCH.PARENT_DIR, cutPrefix = cutPrefix) shouldBe expectedCutPathDir
        }
    }
})
