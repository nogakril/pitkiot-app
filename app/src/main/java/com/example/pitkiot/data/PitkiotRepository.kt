package com.example.pitkiot.data

import com.example.pitkiot.data.models.*

// ktlint-disable no-wildcard-imports

class PitkiotRepository(
    private val pitkiotApi: PitkiotApi
) {
    // remove
    fun getNextWord(): String {
        val words = arrayOf("apple", "banana", "cherry", "date", "elderberry", "fig", "grape")
        return words.random()
    }

    suspend fun createGame(url: String, nickName: String): Result<GameCreationResponse> {
        val body = GameCreationJson(nickName)
        val response = pitkiotApi.createGame(url, body)
        return when {
            response.isSuccessful && response.body() != null -> {
                val createGameResponse = response.body()!!
                Result.success(
                    GameCreationResponse(
                        gameId = createGameResponse.gameId
                    )
                )
            }
            else -> Result.failure(GameError("Error: Could not create a new game"))
        }
    }

    suspend fun joinGame(url: String, gameId: String, nickName: String): Result<TeamGetterResponse> {
        val body = TeamGetterJson(nickName)
        val response = pitkiotApi.joinGame(url, gameId, body)
        return when {
            response.isSuccessful && response.body() != null -> {
                val joinGameResponse = response.body()!!
                Result.success(
                    TeamGetterResponse(
                        team = joinGameResponse.team
                    )
                )
            }
            else -> Result.failure(GameError("Error: Could not join game $gameId"))
        }
    }

    suspend fun getPlayers(url: String, gameId: String, nickName: String): Result<PlayersGetterResponse> {
        val response = pitkiotApi.getPlayers(url, gameId)
        return when {
            response.isSuccessful && response.body() != null -> {
                val getPlayersResponse = response.body()!!
                Result.success(
                    PlayersGetterResponse(
                        players = getPlayersResponse.players
                    )
                )
            }
            else -> Result.failure(GameError("Error: Could not get the list of players for game $gameId"))
        }
    }

    suspend fun addWord(url: String, gameId: String, word: String): Result<Unit> {
        val body = WordAdderJson(word)
        val response = pitkiotApi.addWord(url, gameId, body)
        return when {
            response.isSuccessful -> Result.success(Unit)
            else -> Result.failure(GameError("Error: Could not add the word $word to game $gameId"))
        }
    }

    suspend fun getWords(url: String, gameId: String): Result<WordsGetterResponse> {
        val response = pitkiotApi.getWords(url, gameId)
        return when {
            response.isSuccessful && response.body() != null -> {
                val wordsGetterResponse = response.body()!!
                Result.success(
                    WordsGetterResponse(
                        words = wordsGetterResponse.words
                    )
                )
            }
            else -> Result.failure(GameError("Error: Could not add get the words of game $gameId"))
        }
    }

    class GameError(error: String) : Exception(error)
}

