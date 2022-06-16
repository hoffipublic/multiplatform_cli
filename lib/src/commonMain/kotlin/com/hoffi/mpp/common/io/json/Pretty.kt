package com.hoffi.mpp.common.io.json

import kotlinx.serialization.json.Json

//const val RElinebreak = "\\R"
const val REParOpen = "[{(\\[]"
const val REParClos = "[})\\]]"
// kotlin native workaround for kotlin < 1.7 (can be replaced by \\R)
//const val newline = "\u000D\u000A|[\u000A\u000B\u000C\u000D\u0085\u2028\u2029]"
//val RECombineParsOpen = Regex("(?<=$REParOpen)\\s*$newline\\s*(?=$REParOpen)", RegexOption.MULTILINE)
//val RECombineParsClos = Regex("(?<=$REParClos)\\s*$newline\\s*(?=$REParClos)", RegexOption.MULTILINE)
//val RECompressCurlies = Regex("},\\s*$newline\\s+\\{", RegexOption.MULTILINE)
val RECombineParsOpen = Regex("(?<=$REParOpen)\\s*\\R\\s*(?=$REParOpen)", RegexOption.MULTILINE)
val RECombineParsClos = Regex("(?<=$REParClos)\\s*\\R\\s*(?=$REParClos)", RegexOption.MULTILINE)
val RECompressCurlies = Regex("},\\s*\\R\\s+\\{", RegexOption.MULTILINE)

fun String.collapseParentheses(): String {
    var s = this
    s = s.replace(RECombineParsOpen, "") // combine opening parenthesises on separate lines
    s = s.replace(RECombineParsClos, "") // same on closing ones
    s = s.replace(RECompressCurlies, "},{")
    return s
}
object Pretty {
    val JSON = Json { prettyPrint = true; prettyPrintIndent = " ".repeat(2) }
}
