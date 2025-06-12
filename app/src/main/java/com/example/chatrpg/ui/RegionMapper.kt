package com.example.chatrpg.ui

import com.example.chatrpg.R

fun getBackgroundForRegion(region: String?): Int {
    return when (region?.trim()?.lowercase()) {
        "밴들시티" -> R.drawable.bandlecity_bg
        "빌지워터" -> R.drawable.bilgewater_bg
        "데마시아" -> R.drawable.demacia_bg
        "프렐요드" -> R.drawable.frejlord_bg
        "아이오니아" -> R.drawable.ionia_bg
        "녹서스" -> R.drawable.noxus_bg
        "필트오버" -> R.drawable.piltover_bg
        "공허" -> R.drawable.void_bg
        "숲" -> R.drawable.forest_bg
        else -> R.drawable.forest_bg // 기본 배경 설정 (숲 배경)
    }
}
