package sudoku

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun main() {
    embeddedServer(Netty, port = 40000) {
        install(WebSockets)
        routing {
            webSocket("/chat") {
                send("Please enter your name")
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val name = frame.readText()
                        send("Hello, $name!")
                    }
                }
            }
        }
    }.start(wait = true)
}