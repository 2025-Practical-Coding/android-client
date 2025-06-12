package com.example.chatrpg.model

data class ChatResponse(
    val region: String,        // 현재 지역
    val character: CharacterSimple, // AI NPC 캐릭터 정보
    val userInput: String,    // 사용자의 입력 메시지
    val reply: String,         // AI의 응답
    val delta: Int,            // 변화량 (예: 친밀도 변화)
    val narration: String,     // 스토리 텍스트 (내러티브)
    val totalAffinity: Int,   // 총 친밀도
    val convCount: Int,       // 대화 횟수
    val convLimit: Int,       // 대화 한계
    val allies: List<CharacterSimple>? = null
)

data class CharacterSimple(
    val slug: String,
    val name: String,
    val subtitle: String
)

data class ChatEndWrapper(
    val responses: List<ChatResponse>,
    val gameOver: Boolean
)