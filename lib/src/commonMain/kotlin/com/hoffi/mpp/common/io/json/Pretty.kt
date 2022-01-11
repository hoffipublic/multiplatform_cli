package com.hoffi.mpp.common.io.json

import kotlinx.serialization.json.Json

//const val RElinebreak = "\\R"
const val REParOpen = "[{(\\[]"
const val REParClos = "[})\\]]"
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
