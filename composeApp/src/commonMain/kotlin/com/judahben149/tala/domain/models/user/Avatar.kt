package com.judahben149.tala.domain.models.user

import com.judahben149.tala.domain.models.speech.Gender

enum class Avatar(
    val avatarName: String,
    val gender: Gender
) {
    AIDEN("Aiden", Gender.FEMALE),
    AIDAN("Aidan", Gender.FEMALE),
    ALEXANDER("Alexander", Gender.FEMALE),
    ANDREA("Andrea", Gender.FEMALE),
    CHRISTOPHER("Christopher", Gender.FEMALE),
    BRIAN("Brian", Gender.MALE),
    EDEN("Eden", Gender.FEMALE),
    ELIZA("Eliza", Gender.MALE),
    GEORGE("George", Gender.FEMALE),
    JACK("Jack", Gender.MALE),
    JADE("Jade", Gender.FEMALE),
    JESSICA("Jessica", Gender.FEMALE),
    JUDE("Jude", Gender.FEMALE),
    MASON("Mason", Gender.MALE),
    RILEY("Riley", Gender.FEMALE),
    RYAN("Ryan", Gender.MALE),
    SARAH("Sarah", Gender.FEMALE),
    SAWYER("Sawyer", Gender.FEMALE),
    SOPHIA("Sophia", Gender.MALE),
    WYATT("Wyatt", Gender.FEMALE),
}