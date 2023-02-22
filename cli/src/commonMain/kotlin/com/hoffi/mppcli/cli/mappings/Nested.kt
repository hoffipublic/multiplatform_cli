package com.hoffi.mppcli.cli.mappings

import kotlinx.serialization.Serializable

@Serializable
data class Nested(
    val accounting: Array<Person>,
    val sales: Array<Person>
)

@Serializable
data class Person(
    val firstName: String,
    val lastName: String,
    val age: Int,
)
