package com.hoffi.mppcli.lib.common.io.output

object OutputFrame {
    // @formatter:off
    const val HTOP =       "╭╴"
    const val HMIDDLE =    "│ "
    const val HMIDDLESEP = "├─"
    const val OTOP =       "╭╴"
    const val OMIDDLE =    "│ "
    const val OBOTTOM =    "╰─"
    const val MIN = 25
    // @formatter:on

    enum class WHERE { FIRST, MIDDLE, LAST }
    data class LINES(val s: String, val max: Int)

    fun cmdHeader(s: String): LINES {
        val lines = s.split("\n")
        val max = lines.maxOf { it.length }
        return LINES(lines.joinToString("\n$HMIDDLE", HTOP, "\n$HMIDDLESEP${"─".repeat(max.coerceAtLeast(MIN))}"), max)
    }

    fun cmdOutput(s: String, hasHeader: Boolean = true, maxSoFar: Int = 25, where: WHERE = WHERE.MIDDLE): LINES {
        val lines = s.split("\n")
        val max = lines.maxOf { it.length }
        return LINES(when (where) {
            WHERE.MIDDLE -> lines.joinToString("\n$OMIDDLE", OMIDDLE)
            WHERE.FIRST  -> lines.joinToString("\n$OMIDDLE", OTOP)
            WHERE.LAST   -> lines.joinToString("\n$OMIDDLE", OBOTTOM)
        }, max.coerceAtLeast(maxSoFar))
    }

    fun cmdEnd(max: Int = 25): String {
        return OBOTTOM + "─".repeat(max.coerceAtLeast(MIN))
    }
}
