package com.example.chatrpg.repository

import com.example.chatrpg.model.*

interface ChatRepository {

    /**
     * 오프닝 텍스트 및 첫 캐릭터 slug
     */
    suspend fun getOpening(): OpeningResponse

    /**
     * 사용자 입력을 기반으로 한 AI 응답
     * 경우에 따라 다음 중 하나 반환:
     * 1. 단일 응답 → ChatResponse
     * 2. 대화 종료 → List<ChatResponse>
     * 3. 게임 종료 → GameResultResponse
     */
    suspend fun postChat(request: ChatRequest): Any // ChatResponse | List<ChatResponse> | GameResultResponse

    /**
     * 현재 지역 및 캐릭터 상태 정보
     */
    suspend fun getState(): StateResponse

    /**
     * 게임 종료 여부 및 최종 결과
     */
    suspend fun getResult(): GameResultResponse
}
