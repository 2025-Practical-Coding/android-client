package com.example.chatrpg.model

data class ChatRequest(
    val slug: String,       // AI NPC 캐릭터 식별자
    val name: String,
    val userInput: String  // 사용자가 보낸 메시지
)
