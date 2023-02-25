package com.example.pitkiot.data

/* ktlint-disable */
import com.example.pitkiot.data.models.*
import retrofit2.http.*
/* ktlint-enable */
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

interface PitkiotApi {

    @POST("/")
    suspend fun createGame(
        @Body body: GameCreationJson
    ): Response<GameCreationResponse>

    @PUT(BASE_PLAYER_ADDER_URL)
    suspend fun addPlayer(
        @Query("gameId") gameId: String,
        @Body body: PlayerAdderJson
    ): Response<Unit>

    @GET(BASE_PLAYERS_GETTER_URL)
    suspend fun getPlayers(
        @Query("gameId") gameId: String
    ): Response<PlayersGetterResponse>

    @PUT(BASE_WORD_ADDER_URL)
    suspend fun addWord(
        @Query("gameId") gameId: String,
        @Body body: WordAdderJson
    ): Response<Unit>

    @GET(BASE_WORDS_GETTER_URL)
    suspend fun getWords(
        @Query("gameId") gameId: String
    ): Response<WordsGetterResponse>

    @GET(BASE_STATUS_GETTER_URL)
    suspend fun getStatus(
        @Query("gameId") gameId: String
    ): Response<StatusGetterResponse>

    @PUT(BASE_STATUS_SETTER_URL)
    suspend fun setStatus(
        @Query("gameId") gameId: String,
        @Body body: StatusSetterJson
    ): Response<Unit>

    companion object {
        private const val BASE_GAME_CREATOR_URL = "https://2fd7cttxuprcyyav2ybdhzk4hi0ywbtr.lambda-url.us-west-2.on.aws/"
        private const val BASE_PLAYER_ADDER_URL = "https://sjaxvgnhxa5vmiiew7cnr5anmi0aftdh.lambda-url.us-west-2.on.aws/"
        private const val BASE_STATUS_SETTER_URL = "https://ebs2llc3ixerfgkboyd6gfmxsa0qxwom.lambda-url.us-west-2.on.aws/"
        private const val BASE_STATUS_GETTER_URL = "https://q4i3ok63vpncmuvgd3xmaw4of40ijbds.lambda-url.us-west-2.on.aws/"
        private const val BASE_PLAYERS_GETTER_URL = "https://y2ptad7vg7pl2raly7ebo7asti0hjojt.lambda-url.us-west-2.on.aws/"
        private const val BASE_WORD_ADDER_URL = "https://o2e6gr76txdn4f5w3dtodgvmb40ckyqb.lambda-url.us-west-2.on.aws/"
        private const val BASE_WORDS_GETTER_URL = "https://bn7hrwlgyyveylitohyguntmnu0rcfya.lambda-url.us-west-2.on.aws/"

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
                .baseUrl(BASE_GAME_CREATOR_URL)
                .client(httpClient)
                .build()
        }
    }
}