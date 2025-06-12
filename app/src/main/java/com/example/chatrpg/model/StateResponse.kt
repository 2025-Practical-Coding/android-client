package com.example.chatrpg.model

data class StateResponse(
    val region: String,
    val currentCharacter: CharacterInfo?,  // null 허용
    val totalRemaining: Int,
    val convLimit: Int,
    val maxAffinity: Int
)

data class CharacterInfo(
    val slug: String,
    val name: String,
    val subtitle: String,
    val affinity: Int,
    val convCount: Int
)

data class OpeningResponse(
    val opening: String,
    val currentCharacter: CharacterInfo
)
