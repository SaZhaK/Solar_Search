package com.solanteq.solar.service

import com.solanteq.solar.dto.LineDto

/**
 * @author akolomiets
 * @since 1.0.0
 */
interface SearchService {
    fun search(searchRequest: String): List<LineDto>

    fun searchSnippet(filePath: String, lineNumber: Int): List<String>
}