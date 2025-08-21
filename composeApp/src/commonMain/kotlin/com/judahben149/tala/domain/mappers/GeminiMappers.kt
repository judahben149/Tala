package com.judahben149.tala.domain.mappers

import com.judahben149.tala.data.model.network.gemini.CandidateDto
import com.judahben149.tala.data.model.network.gemini.ContentDto
import com.judahben149.tala.data.model.network.gemini.GeminiResponseDto
import com.judahben149.tala.data.model.network.gemini.PartDto
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