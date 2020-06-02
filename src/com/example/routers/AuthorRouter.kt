package com.example.routers

import com.example.service.AuthorService
import com.example.tables.pojos.Author
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.request.receive
import io.ktor.response.respond
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import com.example.common.Result
import io.ktor.application.ApplicationCall
import io.ktor.http.cio.websocket.Frame
import io.ktor.routing.*
import io.ktor.websocket.webSocket
import java.util.concurrent.TimeUnit

/**
 * @author carl
 *
 */
@JvmOverloads
fun Application.author(testing: Boolean = false) {
    val authorService by kodein().instance<AuthorService>()

    routing {
        if (!"production".equals(environment.config.property("ktor.env")?.getString())) {
            trace { application.log.trace(it.buildText()) }
        }

        route("/author") {
            get {
                val list: List<Author> = authorService.authors()
                call.success(list)
            }

            post {
                val dto = call.receive<Author>()
                authorService.createAuthor(dto)
                call.success()
            }

            delete("/{id}") {
                val id: Long = call.parameters["id"]!!.toLong()
                authorService.deleteAuthor(id)
                call.success()
            }
        }

        webSocket("/echo") {
            var i = 0
            while (true) {
                outgoing.send(Frame.Text("hello hello " + i++))
                flush()
                TimeUnit.SECONDS.sleep(1)
            }
        }
    }
}

suspend inline fun <T> ApplicationCall.success(data: T) {
    respond(Result.success(data))
}

suspend inline fun ApplicationCall.success() {
    respond(Result.success())
}