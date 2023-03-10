package com.solanteq.solar.endpoint

import com.solanteq.solar.dto.SearchResponseDto
import com.solanteq.solar.service.impl.SearchServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

/**
 * @author akolomiets
 * @since 1.0.0
 */
@RestController
@CrossOrigin(origins = ["\${app.dev.frontend.local}"])
class SearchController @Autowired constructor(
        private val searchServiceImpl: SearchServiceImpl
) {

    @RequestMapping(value = ["/search/{request}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun search(@PathVariable("request") request: String): SearchResponseDto {
        val lines = searchServiceImpl.search(request)
        return SearchResponseDto(lines)
    }

    @RequestMapping(value = ["/search/snippet"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun snippet(@RequestParam("linePath") linePath: String,
                @RequestParam("lineNumber") lineNumber: Int): List<String> {
        return searchServiceImpl.searchSnippet(linePath, lineNumber)
    }
}