package com.example.chatrpg.model


data class GameResultResponse(
    val gameOver: Boolean,
    val narration: String,
    val result: String,
    val relationship: Int,
    val allies: Int
)
