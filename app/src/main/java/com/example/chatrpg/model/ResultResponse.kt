package com.example.chatrpg.model

data class ResultResponse(
    val gameOver: Boolean,
    val result: String
)

data class GameResultResponse(
    val game_over: Boolean,
    val narration: String,
    val result: String,
    val relationship: Int,
    val alliies: Int
)
