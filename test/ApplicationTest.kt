package com.example

import com.example.Tables.AUTHOR
import com.example.enums.Gender
import com.example.tables.daos.AuthorDao
import com.typesafe.config.ConfigFactory
import io.ktor.application.Application
import io.ktor.config.HoconApplicationConfig
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import kotlin.test.*
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.codegen.GenerationTool
import org.jooq.impl.DefaultConfiguration
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Configuration
import org.junit.BeforeClass
import org.slf4j.LoggerFactory
import java.sql.DriverManager


class ApplicationTest {

    companion object {
        @KtorExperimentalAPI
        val engine = TestApplicationEngine(createTestEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load("application-test.conf"))
        })

        @BeforeClass
        @JvmStatic
        fun setup() {
//            logger.debug("Starting application with config ....")
            engine.start(wait = false)
            engine.application.module(true)
        }
    }

    @Test
    fun testRoot() {
        with(engine) {

            handleRequest(HttpMethod.Get, "/author").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                println(response.content)
//                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }

    @Ignore
    @Test
    fun `jooq gen`() {
        val configuration = Configuration()
            .withJdbc(
                Jdbc()
                    .withDriver("com.mysql.cj.jdbc.Driver")
                    .withUrl("jdbc:mysql://localhost:3306/jooq_learn")
                    .withUser("root")
                    .withPassword("password")
            )
            .withGenerator(
                Generator()
                    .withName("org.jooq.codegen.JavaGenerator")
                    .withStrategy(Strategy().withName("org.jooq.codegen.DefaultGeneratorStrategy"))
                    .withGenerate(Generate().withPojos(true).withComments(true).withDaos(true))
                    .withDatabase(
                        Database()
                            .withName("org.jooq.meta.mysql.MySQLDatabase")
                            .withIncludes(".*")
                            .withExcludes("")
                            .withInputSchema("jooq_learn")
                            .withForcedTypes(
                                ForcedType()
                                    .withUserType("com.example.domain.Gender")
                                    .withEnumConverter(true)
                                    .withIncludeExpression(".*\\.GENDER")
                                    .withIncludeTypes(".*")
                            )
                            .withIncludePrimaryKeys(true)
                    )
                    .withTarget(
                        org.jooq.meta.jaxb.Target()
                            .withPackageName("com.example")
                            .withDirectory("build/generated-src")
                    )
            )
            .withLogging(Logging.WARN)
            .withOnError(OnError.LOG)

        GenerationTool.generate(configuration)
    }

    @Test
    fun `jooq dsl`() {
        DSL.using("jdbc:mysql://localhost:3306/jooq_learn", "root", "password").use { ctx ->
            ctx.delete(AUTHOR).where(AUTHOR.ID.eq(1L)).execute()
            // insert
            ctx.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.GENDER)
                .values(1L, "小红", "lucy", Gender.FEMALE).execute()
            // select
            ctx.select(AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.GENDER).from(AUTHOR)
                .forEach { println("${it[AUTHOR.FIRST_NAME]}, ${it[AUTHOR.LAST_NAME]}, ${it[AUTHOR.GENDER]}") }

            // select into pojo
            val dto = ctx.select(AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME, AUTHOR.GENDER)
                .from(AUTHOR).where(AUTHOR.ID.eq(1L))
                .fetchOneInto(com.example.tables.pojos.Author::class.java)
            println("dto print")
            println(dto)
        }
    }

    @Test
    fun `load config`() {
        class DBConf(val url: String, val user: String, val password: String)

        val log = LoggerFactory.getLogger("db")
        val config = ConfigFactory.load()
        val hoconApplicationConfig = HoconApplicationConfig(config)
        log.debug(hoconApplicationConfig.property("db.url").getString())
        println(hoconApplicationConfig.property("db.url").getString())

        config.getConfig("db").entrySet().forEach { e -> println(e.key + ", " + e.value.unwrapped()) }
    }

    @Test
    fun `using dao`() {
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jooq_learn", "root", "password")
        val configuration = DefaultConfiguration().set(conn).set(SQLDialect.MYSQL)
        val dao = AuthorDao(configuration)
        val list = dao.findAll()
        list.forEach { author -> println(author.firstName) }
    }
}
