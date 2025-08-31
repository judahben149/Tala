package com.judahben149.tala.util

import com.judahben149.tala.domain.models.speech.Gender
import com.judahben149.tala.domain.models.user.Avatar

object AvatarUrlGenerator {

    fun generate(gender: Gender): String {
        val filteredAvatars = Avatar.entries.filter { avatar ->
            avatar.gender == gender
        }

        return DICE_BEAR_URL.plus(filteredAvatars.random().name)
    }
}