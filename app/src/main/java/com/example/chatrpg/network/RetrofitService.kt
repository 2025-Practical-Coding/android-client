package com.example.chatrpg.network

import com.example.chatrpg.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitService {

    /**
     * 오프닝 정보 요청
     * 응답: OpeningResponse (opening 텍스트 + 캐릭터 slug)
     */
    @GET("/opening")
    suspend fun getOpening(): Response<OpeningResponse>

    /**
     * 대화 요청
     * 요청: ChatRequest(slug, user_input)
     * 응답: ChatEndWrapper
     */
    @POST("/chat")
    suspend fun postChat(@Body request: ChatRequest): Response<ResponseBody>

    /**
     * 현재 지역 및 캐릭터 상태 요청
     * 응답: StateResponse
     */
    @GET("/state")
    suspend fun getState(): Response<StateResponse>

    /**
     * 게임 결과 요청
     * 응답: GameResultResponse
     */
    @GET("/result")
    suspend fun getResult(): Response<GameResultResponse>
}