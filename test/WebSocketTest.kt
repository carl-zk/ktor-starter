package com.example

import com.example.config.http
import com.example.config.kodein
import com.example.config.routers
import io.ktor.application.install
import io.ktor.config.MapApplicationConfig
import io.ktor.http.*
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.routing
import io.ktor.websocket.WebSockets
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.websocket.webSocket
import io.netty.handler.codec.http.HttpHeaders.addHeader
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

/**
 * @author carl
 *
 */
class WebSocketTest {

    @Ignore
    @Test
    fun testA() {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
//                url = "jdbc:mysql://localhost:3306/jooq_learn"
//                user = "root"
//                password = "password"
                put("ktor.env", "dev")
                put("db.url", "jdbc:mysql://localhost:3306/jooq_learn")
                put("db.user", "root")
                put("db.password", "password")
            }
            kodein()
            routers()
            http()
        }) {
            with(handleRequest(HttpMethod.Get, "/author")) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello from Ktor Testable sample application", response.content)
            }
        }
    }

    @Ignore
    @Test
    fun testConversation() {
        withTestApplication {
            application.install(WebSockets)

            val received = arrayListOf<String>()
            application.routing {
                webSocket("/echo") {
                    try {
                        while (true) {
                            val text = (incoming.receive() as Frame.Text).readText()
                            received += text
                            outgoing.send(Frame.Text(text))
                        }
                    } catch (e: ClosedReceiveChannelException) {
                        // Do nothing!
                        e.printStackTrace()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }

            handleWebSocketConversation("/echo") { incoming, outgoing ->
                val textMessages = listOf("HELLO", "WORLD")
                for (msg in textMessages) {
                    outgoing.send(Frame.Text(msg))
                    assertEquals(msg, (incoming.receive() as Frame.Text).readText())
                }
                assertEquals(textMessages, received)
            }
        }
    }
}