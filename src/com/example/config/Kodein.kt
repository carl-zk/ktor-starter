package com.example.config

import com.example.service.AuthorService
import com.example.tables.daos.AuthorDao
import com.mysql.cj.jdbc.MysqlDataSource
import io.ktor.application.Application
import io.ktor.application.ApplicationEnvironment
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.impl.DefaultConfiguration
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.kodein

/**
 * @author carl
 *
 */
@JvmOverloads
fun Application.kodein(testing: Boolean = false) {
    val configuration = loadDB(environment)
    kodein {
        bind<AuthorDao>() with singleton { AuthorDao(configuration) }
        bind<AuthorService>() with singleton { AuthorService( kodein()) }
    }
}

fun loadDB(env: ApplicationEnvironment): Configuration {
    val dbConf = env.config.config("db")
    val datasource = MysqlDataSource()
    datasource.setURL(dbConf.property("url").getString())
    datasource.setUser(dbConf.property("user").getString())
    datasource.setPassword(dbConf.property("password").getString())

    return DefaultConfiguration().set(datasource).set(SQLDialect.MYSQL)
}