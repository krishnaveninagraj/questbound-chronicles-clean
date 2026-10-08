package com.edgechild.ai

import androidx.annotation.DrawableRes

data class IllustrationInfo(
    val category: String,
    val emoji: String,
    @DrawableRes val drawableRes: Int
)

object IllustrationMapper {
    fun getIllustration(query: String, responseText: String, tag: String? = null): IllustrationInfo {
        if (!tag.isNullOrEmpty()) {
            when (tag.uppercase()) {
                "[AVATAR: SPACE]" -> return IllustrationInfo("Space & Astronomy", "🚀", R.drawable.ic_illustration_space)
                "[AVATAR: NATURE]" -> return IllustrationInfo("Nature & Earth", "🌍", R.drawable.ic_illustration_sky)
                "[AVATAR: ANIMALS]" -> return IllustrationInfo("Animals & Wildlife", "🐶", R.drawable.ic_illustration_animal)
                "[AVATAR: SCIENCE]" -> return IllustrationInfo("Curiosity & Science", "🧪", R.drawable.ic_illustration_default)
            }
        }
        
        return IllustrationInfo("Curiosity & Science", "🧪", R.drawable.ic_illustration_default)
    }
}
