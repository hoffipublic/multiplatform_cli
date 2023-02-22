package com.hoffi.mppcli.lib.common.io.fs

import kotlinx.cinterop.*
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.PATH_MAX
import platform.posix.getcwd

actual val fs: FileSystem = FileSystem.SYSTEM
/** see https://github.com/korlibs/korge/blob/fa3a118cdcc05bcb52303afe67e93e396fabe279/korio/src/linuxMain/kotlin/com/soywiz/korio/file/std/StandardBasePathsJs.kt */
actual fun FileSystem.cwd(): Path = memScoped {
    val temp = allocArray<ByteVar>(PATH_MAX + 1)
    getcwd(temp, PATH_MAX.convert())
    temp.toKString()
}.toPath(normalize = true)
