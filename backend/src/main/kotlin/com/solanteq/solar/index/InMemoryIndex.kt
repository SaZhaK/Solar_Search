package com.solanteq.solar.index

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import kotlin.io.path.pathString
import kotlin.streams.toList

/**
 * @author akolomiets
 * @since 1.0.0
 */
@Component
class InMemoryIndex {
    private val analyzer = StandardAnalyzer()
    private val memoryIndex = RAMDirectory()

    @PostConstruct
    fun initIndex() {
        indexDirectory(BASE_DIR)
    }

    @PreDestroy
    fun closeIndex() {
        memoryIndex.close()
    }

    fun indexDirectory(path: String): Directory {
        val indexWriterConfig = IndexWriterConfig(analyzer)
        val indexWriter = IndexWriter(memoryIndex, indexWriterConfig)

        fun indexFile(fullPath: String) {
            val lines = readFile(fullPath)

            lines.forEachIndexed { index, lineContent ->
                val lineNumber = (index + 1).toString()

                val document = Document()
                document.add(TextField(CONTENT, lineContent, Field.Store.YES))
                document.add(TextField(PATH, fullPath.removePrefix("$CONFIG_DIR/"), Field.Store.YES))
                document.add(TextField(LINE_NUMBER, lineNumber, Field.Store.YES))

                indexWriter.addDocument(document)
            }
        }

        val configPath = Paths.get(CONFIG_DIR)
        val fullPath = configPath.resolve(Paths.get(path))
        Files.walk(fullPath).use { stream ->
            stream.filter(Files::isRegularFile).forEach {
                indexFile(it.pathString)
            }
        }

        indexWriter.close()
        return memoryIndex
    }

    fun searchIndex(query: Query): List<Document> {
        val indexReader = DirectoryReader.open(memoryIndex)
        val searcher = IndexSearcher(indexReader)

        val topDocs = searcher.search(query, RESULTS_LIMIT)
        return topDocs.scoreDocs
                .map { searcher.doc(it.doc) }
                .toList()
    }

    private fun readFile(fullPath: String): List<String> {
        val lines = Files.lines(Paths.get(fullPath))
        val data = lines.toList()
        lines.close()
        return data
    }

    companion object Constants {
        const val CONTENT = "content"
        const val PATH = "path"
        const val LINE_NUMBER = "lineNumber"

        const val RESULTS_LIMIT = Integer.MAX_VALUE

        // TODO
        const val CONFIG_DIR = "/home/sazha/SaZha/Java projects/Solar Search"
        const val BASE_DIR = "backend/src/main/java/dev/marco/example/springboot"
    }
}