package com.example.pitkiot.data

// ktlint-disable no-wildcard-imports
import com.example.pitkiot.data.models.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface PitkiotApi {

    @POST("/")
    suspend fun createGame(
        @Url url: String = URL,
        @Body body: GameCreationJson
    ): Response<GameCreationResponse>

    @PUT("/players")
    suspend fun joinGame(
        @Url url: String = URL,
        @Query("gameId") gameId: String,
        @Body body: TeamGetterJson
    ): Response<TeamGetterResponse>

    @GET("/players")
    suspend fun getPlayers(
        @Url url: String = URL,
        @Query("gameId") gameId: String
    ): Response<PlayersGetterResponse>

    @PUT("/words")
    suspend fun addWord(
        @Url url: String = URL,
        @Query("gameId") gameId: String,
        @Body body: WordAdderJson
    ): Response<Unit>

    @PUT("/words")
    suspend fun getWords(
        @Url url: String = URL,
        @Query("gameId") gameId: String
    ): Response<WordsGetterResponse>

    companion object {
        private const val URL = "https://hvvevwwvae52ztpmfb4ftfjg3u0tqlxa.lambda-url.us-west-2.on.aws/"

        val instance: PitkiotApi by lazy {
            val retrofit: Retrofit = createRetrofit()
            retrofit.create(PitkiotApi::class.java)
        }

        private fun createRetrofit(): Retrofit {
            // Create converter
            val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            // Create logger
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            // Create client
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            // Build Retrofit
            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(URL)
                .client(httpClient)
                .build()
        }
    }
}