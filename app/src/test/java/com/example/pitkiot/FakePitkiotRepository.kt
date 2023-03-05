package com.example.pitkiot

import com.example.pitkiot.data.GameError
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.enums.GameStatus.ADDING_PLAYERS
import com.example.pitkiot.data.models.GameCreationResponse
import com.example.pitkiot.data.models.PlayersGetterResponse
import com.example.pitkiot.data.models.StatusGetterResponse
import com.example.pitkiot.data.models.WordsGetterResponse
import okio.IOException

class FakePitkiotRepository(private val state: FakeRepositoryState) : PitkiotRepository {
    override suspend fun createGame(nickName: String): Result<GameCreationResponse> {
        when (state) {
            FakeRepositoryState.NoInternet -> {
                throw IOException()
            }
            FakeRepositoryState.Success -> {
                val mockResponse = GameCreationResponse(gameId = "jdj73858jj3mf7h")
                return Result.success(mockResponse)
            }
            FakeRepositoryState.Failure -> {
                return Result.failure(GameError("error"))
            }
        }
    }

    override suspend fun addPlayer(gameId: String, nickName: String): Result<Unit> {
        return when (state) {
            FakeRepositoryState.NoInternet -> {
                throw IOException()
            }
            FakeRepositoryState.Success -> {
                Result.success(Unit)
            }
            FakeRepositoryState.Failure -> {
                Result.failure(GameError("error"))
            }
        }
    }

    override suspend fun getPlayers(gameId: String): Result<PlayersGetterResponse> {
        return when (state) {
            FakeRepositoryState.NoInternet -> {
                throw IOException()
            }
            FakeRepositoryState.Success -> {
                val players = listOf("Noga", "Omri", "John", "Mike")
                val mockResponse = PlayersGetterResponse(players = players)
                Result.success(mockResponse)
            }
            FakeRepositoryState.Failure -> {
                Result.failure(GameError("error"))
            }
        }
    }

    override suspend fun addWord(gameId: String, word: String): Result<Unit> {
        return when (state) {
            FakeRepositoryState.NoInternet -> {
                throw IOException()
            }
            FakeRepositoryState.Success -> {
                val players = listOf("Noga", "Omri", "John", "Mike")
                val mockResponse = PlayersGetterResponse(players = players)
                Result.success(Unit)
            }
            FakeRepositoryState.Failure -> {
                Result.failure(GameError("error"))
            }
        }
    }

    override suspend fun getWords(gameId: String): Result<WordsGetterResponse> {
        return when (state) {
            FakeRepositoryState.NoInternet -> {
                throw IOException()
            }
            FakeRepositoryState.Success -> {
                val words = listOf("apple", "banana", "cherry", "date", "elderberry", "fig", "grape")
                val mockResponse = WordsGetterResponse(words = words)
                return Result.success(mockResponse)
            }
            FakeRepositoryState.Failure -> {
                Result.failure(GameError("error"))
            }
        }
    }

    override suspend fun getStatus(gameId: String): Result<StatusGetterResponse> {
        return when (state) {
            FakeRepositoryState.NoInternet -> {
                throw IOException()
            }
            FakeRepositoryState.Success -> {
                val mockResponse = StatusGetterResponse(status = ADDING_PLAYERS.statusName)
                return Result.success(mockResponse)
            }
            FakeRepositoryState.Failure -> {
                Result.failure(GameError("error"))
            }
        }
    }

    override suspend fun setStatus(gameId: String, status: GameStatus): Result<Unit> {
        return when (state) {
            FakeRepositoryState.NoInternet -> {
                throw IOException()
            }
            FakeRepositoryState.Success -> {
                return Result.success(Unit)
            }
            FakeRepositoryState.Failure -> {
                Result.failure(GameError("error"))
            }
        }
    }
}