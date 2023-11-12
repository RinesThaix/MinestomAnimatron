package sexy.kostya.animatron

import com.sun.net.httpserver.HttpExchange
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

interface ResourcePackWebServer {

    fun onRequest(request: ResourcePackRequest, exchange: HttpExchange)

    fun onException(e: Exception) {
        System.err.println("Exception caught when serving a resource-pack")
        e.printStackTrace()
    }

    fun onInvalidRequest(exchange: HttpExchange) {
        val response = "Please use a Minecraft client".toByteArray(StandardCharsets.UTF_8)
        exchange.sendResponseHeaders(400, response.size.toLong())
        exchange.responseBody.write(response)
    }

    companion object {
        fun of(pack: File): ResourcePackWebServer {
            return object : ResourcePackWebServer {
                override fun onRequest(request: ResourcePackRequest, exchange: HttpExchange) {
                    val data = Files.readAllBytes(pack.toPath())
                    exchange.responseHeaders["Content-Type"] = "application/zip"
                    exchange.sendResponseHeaders(200, data.size.toLong())
                    exchange.responseBody.write(data);
                }
            }
        }
    }

}