package com.example.chatrpg.model

import com.google.gson.annotations.SerializedName

//
//data class GameResultResponse(
//    val game_over: Boolean,
//    val result: GameResult
//)
//
//data class GameResult(
//    val summary: String,
//    val total_allies: Int,
//    val ally_list: List<String>,
//    val rounds_used: Int
//)


//data class GameResultResponse(
//    val gameOver: Boolean,
//    val result: String
//)

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
