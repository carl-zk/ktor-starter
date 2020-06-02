package com.example.service

import com.example.tables.daos.AuthorDao
import com.example.tables.pojos.Author
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * @author carl
 *
 */
class AuthorService(kodein: Kodein) {
    private val authorDao by kodein.instance<AuthorDao>()

    fun sayHello(): String {
        return "hello"
    }

    fun authors(): List<Author> {
        return authorDao.findAll()
    }

    fun createAuthor(author: Author) {
        authorDao.insert(author)
    }

    fun deleteAuthor(id: Long) {
        authorDao.deleteById(id)
    }
}