package com.example.chatrpg.repository

import com.example.chatrpg.model.*

interface ChatRepository {

    /**
     * 오프닝 텍스트 및 첫 캐릭터 slug
     */
    suspend fun getOpening(): OpeningResponse

    /**
     * 사용자 입력을 기반으로 한 AI 응답
     */
    suspend fun postChat(request: ChatRequest): ChatEndWrapper

    /**
     * 현재 지역 및 캐릭터 상태 정보
     */
    suspend fun getState(): StateResponse

    /**
     * 게임 종료 여부 및 최종 결과
     */
    suspend fun getResult(): GameResultResponse
}
