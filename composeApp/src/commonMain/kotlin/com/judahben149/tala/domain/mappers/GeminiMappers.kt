package com.judahben149.tala.domain.mappers

import com.judahben149.tala.data.model.network.CandidateDto
import com.judahben149.tala.data.model.network.ContentDto
import com.judahben149.tala.data.model.network.GeminiResponseDto
import com.judahben149.tala.data.model.network.PartDto
import com.judahben149.tala.domain.models.gemini.Gemini

fun GeminiResponseDto.toGemini(): Gemini {
    return Gemini(candidates.map { it.toCandidate() })
}

fun CandidateDto.toCandidate(): Gemini.Candidate {
    return Gemini.Candidate(content.toContent())
}

fun ContentDto.toContent(): Gemini.Content {
    return Gemini.Content(parts.map { it.toPart() }, role)
}

fun PartDto.toPart(): Gemini.Part {
    return Gemini.Part(text)
}