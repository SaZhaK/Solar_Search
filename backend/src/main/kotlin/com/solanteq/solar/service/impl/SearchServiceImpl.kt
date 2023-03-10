package com.solanteq.solar.service.impl

import com.solanteq.solar.dto.LineDto
import com.solanteq.solar.index.InMemoryIndex
import com.solanteq.solar.index.InMemoryIndex.Constants.CONTENT
import com.solanteq.solar.index.InMemoryIndex.Constants.LINE_NUMBER
import com.solanteq.solar.index.InMemoryIndex.Constants.PATH
import com.solanteq.solar.service.SearchService
import org.apache.lucene.index.Term
import org.apache.lucene.search.WildcardQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

/**
 * @author akolomiets
 * @since 1.0.0
 */
@Service("sch.searchService")
class SearchServiceImpl @Autowired constructor(
        private val inMemoryIndex: InMemoryIndex
): SearchService {

    override fun search(searchRequest: String): List<LineDto> {
        val term = Term(CONTENT, "*$searchRequest*")
        val query = WildcardQuery(term)
        val documents = inMemoryIndex.searchIndex(query)

        // TODO log ?
        val response = documents.map {
            "${it.getField(CONTENT).stringValue()} : ${it.getField(PATH).stringValue()}"
        }
        println(response)

        val lines = documents.map {
            val content = it.getField(CONTENT).stringValue()
            val path = it.getField(PATH).stringValue()
            val lineNumber = it.getField(LINE_NUMBER).stringValue().toInt()
            LineDto(content, path, lineNumber)
        }

        return lines
    }

    // TODO fix logic
    override fun searchSnippet(filePath: String, lineNumber: Int): List<String> {
        val path = Paths.get(filePath)
        val lines = Files.lines(path)

        val data = lines.toList()
        lines.close()

        val searchedLineIdx = lineNumber - 1;
        val snippet = mutableListOf<String>()
        if (searchedLineIdx >= 2) {
            snippet.add(data[searchedLineIdx - 2])
        }
        if (searchedLineIdx >= 1) {
            snippet.add(data[searchedLineIdx - 1])
        }
        snippet.add(data[searchedLineIdx])
        if (searchedLineIdx <= data.size - 2) {
            snippet.add(data[searchedLineIdx + 1])
        }
        if (searchedLineIdx <= data.size - 3) {
            snippet.add(data[searchedLineIdx + 2])
        }

        return snippet.toList()
    }
}