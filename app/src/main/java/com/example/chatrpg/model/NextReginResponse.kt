package com.example.chatrpg.model

data class NextRegionResponse(
    val regionName: String,
    val characters: List<CharacterDto>,
    val remainingRegions: Int
)


data class CharacterDto(
    val name: String,
    val slug: String,
    val subtitle: String,
    val story: String,
    val affinity: Int,
    val is_ally: Boolean
)
