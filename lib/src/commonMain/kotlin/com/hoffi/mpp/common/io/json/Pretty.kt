package com.hoffi.mpp.common.io.mpp.json

import kotlinx.serialization.json.Json

val RElinebreak = "\\R"
val REparClose = "[})\\]]"
val REparOpen = "[{(\\[]"

fun String.collapseParenthesises(): String {
    var s = this
    s = s.replace("(?<=${REparOpen})\\s*\\R\\s*(?=${REparOpen})".toRegex(RegexOption.MULTILINE), "") // combine opening parenthesises
    // s = s.replace("(?<=${REhelper.parOpen})\\s(?=${REhelper.parClose})".toRegex(RegexOption.MULTILINE), "") // compact opening parenthesises
    s = s.replace("(?<=${REparClose})\\s*\\R\\s*(?=${REparClose})".toRegex(RegexOption.MULTILINE), "") // same on closing
    // s = s.replace("(?<=${REhelper.parClose})\\s(?=${REhelper.parClose})".toRegex(RegexOption.MULTILINE), "") // same on closing
    s = s.replace("},\\s*\\R\\s+\\{".toRegex(RegexOption.MULTILINE), "},{")
    return s
}
object Pretty {
    val JSON = Json { prettyPrint = true; prettyPrintIndent = " ".repeat(2) }
}
