package com.hoffi.mpp.cli.mappings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Buildspec(
    val version: String,
    val phases: Phases,
    val artifacts: Artifacts,
    val cache: Cache
)

@Serializable
data class Phases(
    val install: Install,
    @SerialName("pre_build")
    val preBuild: PreBuild,
    val build: Build,
    @SerialName("post_build")
    val postBuild: PostBuild
)

interface Phase

@Serializable
data class Install(
    @SerialName("runtime-versions")
    val runtimeVersions: RuntimeVersions,
    val commands: Array<String>
) : Phase
@SerialName("runtime-versions")
@Serializable
data class RuntimeVersions(
    val nodejs: String
)
@Serializable
data class Commands(
    val commands: Array<String>
)
@SerialName("pre_build")
@Serializable
data class PreBuild(
    val commands: Array<String>
) : Phase
@Serializable
data class Build(
    val commands: Array<String>
) : Phase
@SerialName("post_build")
@Serializable
data class PostBuild(
    val commands: Array<String>
) : Phase

@Serializable
data class Artifacts(
    val name: String,
    @SerialName("base-directory")
    val baseDirectory: String,
    val files: Array<String>
)
@Serializable
data class Cache (
    val paths: Array<String>
)
