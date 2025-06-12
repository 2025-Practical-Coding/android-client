package com.example.chatrpg.repository

import android.util.Log
import com.example.chatrpg.model.*
import com.example.chatrpg.network.RetrofitInstance
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Response
class RealChatRepository : ChatRepository {

    private val gson = Gson()

    override suspend fun getOpening(): OpeningResponse {
        val response = RetrofitInstance.api.getOpening()
        return response.body() ?: throw Exception("Opening 요청 실패: ${response.code()} - ${response.message()}")
    }

    override suspend fun postChat(request: ChatRequest): ChatEndWrapper {
        val response = RetrofitInstance.api.postChat(request)
        return parseDynamicChatResponse(response)
    }

    override suspend fun getState(): StateResponse {
        val response = RetrofitInstance.api.getState()
        return response.body() ?: throw Exception("State 요청 실패: ${response.code()} - ${response.message()}")
    }

    override suspend fun getResult(): GameResultResponse {
        val response = RetrofitInstance.api.getResult()
        return response.body() ?: throw Exception("Result 요청 실패: ${response.code()} - ${response.message()}")
    }

    /**
     * `/chat`, `/next` 등에서 다양한 응답 형태를 처리하는 공통 로직
     */
    private fun parseDynamicChatResponse(response: Response<ResponseBody>): ChatEndWrapper {
        if (!response.isSuccessful) {
            throw Exception("API 실패: ${response.code()} - ${response.message()}")
        }

        val raw = response.body()?.string() ?: throw Exception("응답 본문이 null입니다.")
        Log.e("RealChatRepository", "📦 Raw Response: $raw")

        val jsonElement = JsonParser.parseString(raw)
        if (!jsonElement.isJsonObject) {
            throw Exception("❗예상치 못한 JSON 응답 형식: $raw")
        }

        val root = jsonElement.asJsonObject

        return when {
            root.has("response") -> {
                // ✅ 일반 단일 응답
                val chatObj = root.getAsJsonObject("response")
                val chatResponse = gson.fromJson(chatObj, ChatResponse::class.java)
                val gameOver = root.get("gameOver")?.asBoolean ?: false
                ChatEndWrapper(responses = listOf(chatResponse), gameOver = gameOver)
            }

            root.has("responses") -> {
                // ✅ 복수 응답 (대화 종료 or 게임 종료)
                val chatArray = root.getAsJsonArray("responses")
                val chatResponses = gson.fromJson(chatArray, Array<ChatResponse>::class.java).toList()
                val gameOver = root.get("gameOver")?.asBoolean ?: false
                ChatEndWrapper(responses = chatResponses, gameOver = gameOver)
            }

            else -> throw Exception("❗예상치 못한 JSON 응답 구조: $raw")
        }
    }

}
