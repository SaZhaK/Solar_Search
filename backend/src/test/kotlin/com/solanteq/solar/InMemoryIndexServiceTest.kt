//package com.solanteq.solar
//
//import com.solanteq.solar.index.InMemoryIndexService
//import org.apache.lucene.index.Term
//import org.apache.lucene.search.FuzzyQuery
//import org.apache.lucene.search.Query
//import org.apache.lucene.search.WildcardQuery
//import org.junit.jupiter.api.Test
//
//class InMemoryIndexServiceTest {
//
//    @Test
//    fun test() {
//        val inMemoryLuceneIndex = InMemoryIndexService()
//        val index = inMemoryLuceneIndex.indexFile("src/main/java/dev/marco/example/springboot/hello/SearchController.java")
//        val term = Term("content", "requestMapping")
//        val query: Query = FuzzyQuery(term)
//        val documents = inMemoryLuceneIndex.searchIndex(index, query, 10)
//        println(documents)
//    }
//
//    @Test
//    fun test2() {
//        val inMemoryLuceneIndex = InMemoryIndexService()
//        inMemoryLuceneIndex.indexDirectory("src/main/java/dev/marco/example/springboot")
//        val term = Term("content", "*feature*")
//        val query: Query = WildcardQuery(term)
//        val documents = inMemoryLuceneIndex.searchIndex(query, 10)
//
//        documents.forEach{
//            println("${it.getField("path").stringValue()} : ${it.getField("content").stringValue()}")
//        }
//    }
//}