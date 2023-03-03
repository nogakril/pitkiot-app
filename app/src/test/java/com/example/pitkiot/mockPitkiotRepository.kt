package com.example.pitkiot

import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.enums.GameStatus.ADDING_PLAYERS
import com.example.pitkiot.data.models.GameCreationResponse
import com.example.pitkiot.data.models.PlayersGetterResponse
import com.example.pitkiot.data.models.StatusGetterResponse
import com.example.pitkiot.data.models.WordsGetterResponse

class mockPitkiotRepository: PitkiotRepository {
    override suspend fun createGame(nickName: String): Result<GameCreationResponse> {
        val mockResponse = GameCreationResponse(gameId = "jdj73858jj3mf7h")
        return Result.success(mockResponse)
    }

    override suspend fun addPlayer(gameId: String, nickName: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getPlayers(gameId: String): Result<PlayersGetterResponse> {
        val players = listOf("Noga", "Omri", "John", "Mike")
        val mockResponse = PlayersGetterResponse(players = players)
        return Result.success(mockResponse)
    }

    override suspend fun addWord(gameId: String, word: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getWords(gameId: String): Result<WordsGetterResponse> {
        val words = listOf("apple", "banana", "cherry", "date", "elderberry", "fig", "grape")
        val mockResponse = WordsGetterResponse(words = words)
        return Result.success(mockResponse)
    }

    override suspend fun getStatus(gameId: String): Result<StatusGetterResponse> {
        val mockResponse = StatusGetterResponse(status = ADDING_PLAYERS.statusName)
        return Result.success(mockResponse)
    }

    override suspend fun setStatus(gameId: String, status: GameStatus): Result<Unit> {
        val mockResponse = StatusGetterResponse(status = status.statusName)
        return Result.success(Unit)
    }
}