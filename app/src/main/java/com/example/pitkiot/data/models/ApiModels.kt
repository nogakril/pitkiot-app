package com.example.pitkiot.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameCreationJson(
    @Json(name = "nickName")
    val nickName: String
)

@JsonClass(generateAdapter = true)
data class GameCreationResponse(
    @Json(name = "gameId")
    val gameId: String
)

@JsonClass(generateAdapter = true)
data class TeamGetterJson(
    @Json(name = "nickName")
    val nickName: String
)

@JsonClass(generateAdapter = true)
data class TeamGetterResponse(
    @Json(name = "team")
    val team: String
)

@JsonClass(generateAdapter = true)
data class PlayersGetterResponse(
    @Json(name = "players")
    val players: List<String>
)

@JsonClass(generateAdapter = true)
data class WordAdderJson(
    @Json(name = "word")
    val word: String
)

@JsonClass(generateAdapter = true)
data class WordsGetterResponse(
    @Json(name = "words")
    val words: List<String>
)