package com.example.pitkiot.data

import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.GameCreationResponse
import com.example.pitkiot.data.models.PlayersGetterResponse
import com.example.pitkiot.data.models.StatusGetterResponse
import com.example.pitkiot.data.models.WordsGetterResponse

interface PitkiotRepository {
    suspend fun createGame(nickName: String): Result<GameCreationResponse>

    suspend fun addPlayer(gameId: String, nickName: String): Result<Unit>

    suspend fun getPlayers(gameId: String): Result<PlayersGetterResponse>

    suspend fun addWord(gameId: String, word: String): Result<Unit>

    suspend fun getWords(gameId: String): Result<WordsGetterResponse>

    suspend fun getStatus(gameId: String): Result<StatusGetterResponse>

    suspend fun setStatus(gameId: String, status: GameStatus): Result<Unit>
}