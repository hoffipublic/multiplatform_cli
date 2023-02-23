package com.hoffi.mppcli.lib.common.io.fs

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import java.io.File

actual val fs: FileSystem = FileSystem.SYSTEM
actual fun cwd(): Path = File(".").absolutePath.toPath(normalize = true) // (File(File(".").absolutePath).canonicalPath.toPath()
