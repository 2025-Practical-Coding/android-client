//package com.example.chatrpg.model
//
//data class StateResponse(
//    val region: String,
//    val current_character: CurrentCharacterInfo?, // null 가능
//    val total_remaining: Int,
//    val current_remaining: Int
//)
//
//data class CurrentCharacterInfo(
//    val slug: String,
//    val name: String,
//    val subtitle: String,
//    val affinity: Int
//)
//
package com.example.chatrpg.model

data class NextRegionResponse(
    val region: String,
    val characters: List<String>
)



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