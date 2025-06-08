package com.example.chatrpg.model

data class StateResponse(
    val region: String,
    val currentCharacter: CharacterInfo?,
    val totalRemaining: Int,
    val currentRemaining: Int
)

data class CharacterInfo(
    val slug: String,
    val name: String,
    val subtitle: String,
    val affinity: Int
)
