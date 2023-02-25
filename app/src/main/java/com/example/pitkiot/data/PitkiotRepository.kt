package com.example.pitkiot.data

/* ktlint-disable */
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.*
/* ktlint-enable */

class PitkiotRepository(
    private val pitkiotApi: PitkiotApi
) {

//    // Mock functions for testing
//    suspend fun createGame(nickName: String): Result<GameCreationResponse> {
//        val mockResponse = GameCreationResponse(gameId = "e4dkd83mf7h")
//        return Result.success(mockResponse)
//    }
//
//    suspend fun joinGame(gameId: String, nickName: String): Result<PlayerAdderResponse> {
//        val mockResponse = PlayerAdderResponse(team = Team.TEAM_A)
//        return Result.success(mockResponse)
//    }
//
//    suspend fun getPlayers(gameId: String): Result<PlayersGetterResponse> {
//        val players = listOf("Noga", "Omri", "John", "Mike")
//        val mockResponse = PlayersGetterResponse(players = players)
//        return Result.success(mockResponse)
//    }
//
//    suspend fun addWord(gameId: String, word: String): Result<Unit> {
//        return Result.success(Unit)
//    }
//
//    suspend fun getWords(gameId: String): Result<WordsGetterResponse> {
//        val words = listOf("apple", "banana", "cherry", "date", "elderberry", "fig", "grape")
//        val mockResponse = WordsGetterResponse(words = words)
//        return Result.success(mockResponse)
//    }
//
//    suspend fun getStatus(gameId: String): Result<StatusGetterResponse> {
//        val mockResponse = StatusGetterResponse(status = GameStatus.ADDING_PLAYERS)
//        return Result.success(mockResponse)
//    }
//
//    suspend fun setStatus(gameId: String): Result<Unit> {
//        val mockResponse = StatusGetterResponse(status = GameStatus.ADDING_PLAYERS)
//        return Result.success(Unit)
//    }

    // Real functions, use when backend ready
    suspend fun createGame(nickName: String): Result<GameCreationResponse> {
        val body = GameCreationJson(nickName)
        val response = pitkiotApi.createGame(body)
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

    suspend fun joinGame(gameId: String, nickName: String): Result<PlayerAdderResponse> {
        val body = PlayerAdderJson(nickName)
        val response = pitkiotApi.joinGame(gameId = gameId, body = body)
        return when {
            response.isSuccessful && response.body() != null -> {
                val joinGameResponse = response.body()!!
                Result.success(
                    PlayerAdderResponse(
                        team = joinGameResponse.team
                    )
                )
            }
            else -> Result.failure(GameError("Error: Could not join game $gameId"))
        }
    }

    suspend fun getPlayers(gameId: String): Result<PlayersGetterResponse> {
        val response = pitkiotApi.getPlayers(gameId = gameId)
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

    suspend fun addWord(gameId: String, word: String): Result<Unit> {
        val body = WordAdderJson(word)
        val response = pitkiotApi.addWord(gameId = gameId, body = body)
        return when {
            response.isSuccessful -> Result.success(Unit)
            else -> Result.failure(GameError("Error: Could not add the word $word to game $gameId"))
        }
    }

    suspend fun getWords(gameId: String): Result<WordsGetterResponse> {
        val response = pitkiotApi.getWords(gameId = gameId)
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

    suspend fun getStatus(gameId: String): Result<StatusGetterResponse> {
        val response = pitkiotApi.getStatus(gameId = gameId)
        return when {
            response.isSuccessful && response.body() != null -> {
                val statusGetterResponse = response.body()!!
                Result.success(
                    StatusGetterResponse(
                        status = statusGetterResponse.status
                    )
                )
            }
            else -> Result.failure(GameError("Error: Could not add get the words of game $gameId"))
        }
    }

    suspend fun setStatus(gameId: String, status: GameStatus): Result<Unit> {
        val body = StatusSetterJson(
            when (status) {
                GameStatus.ADDING_PLAYERS -> "adding_players"
                GameStatus.ADDING_WORDS -> "adding_words"
                else -> { "in_game" }
            }
        )
        val response = pitkiotApi.setStatus(gameId = gameId, body = body)
        return when {
            response.isSuccessful -> Result.success(Unit)
            else -> Result.failure(GameError("Error: Could not set the status of game $gameId to $status"))
        }
    }
}

class GameError(error: String) : Exception(error)