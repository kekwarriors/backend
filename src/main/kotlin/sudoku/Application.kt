package sudoku

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

fun main() {
    val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
        ?: error("KEYSTORE_PASSWORD environment variable not set")

    val keyStoreFile = File("/app/keystore.p12")
    val keyStore = KeyStore.getInstance("PKCS12").apply {
        load(FileInputStream(keyStoreFile), keystorePassword.toCharArray())
    }

    embeddedServer(Netty, configure = {
        sslConnector(
            keyStore = keyStore,
            keyAlias = "ktor",
            keyStorePassword = { keystorePassword.toCharArray() },
            privateKeyPassword = { keystorePassword.toCharArray() }
        ) {
            port = 40000
            keyStorePath = keyStoreFile
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "ktor",
            keyStorePassword = { keystorePassword.toCharArray() },
            privateKeyPassword = { keystorePassword.toCharArray() }
        ) {
            port = 443
            keyStorePath = keyStoreFile
        }
        connector {
            port = 80
        }
    }, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(WebSockets) {
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        // Regular HTTP/HTTPS routes (port 80/443)
        get("/ping") {
            call.respondText("ok")
        }

        webSocket("/ws") {
            if (call.request.local.serverPort != 40000) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Wrong port"))
                return@webSocket
            }
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        send(Frame.Text("Echo: $text"))
                    }
                    else -> {}
                }
            }
        }
    }
}