//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package sexy.kostya.animatron

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetSocketAddress
import java.util.*

class ResourcePackServer private constructor(
    address: InetSocketAddress?,
    path: String,
    handler: ResourcePackWebServer?
) {

    private val server: HttpServer
    private val handler: ResourcePackWebServer?

    init {
        server = HttpServer.create(address, 0)
        this.handler = handler
        server.createContext(path) { exchange: HttpExchange -> handleRequest(exchange) }
    }

    fun httpServer(): HttpServer {
        return server
    }

    fun start() {
        server.start()
    }

    fun stop(delay: Int) {
        server.stop(delay)
    }

    @Throws(IOException::class)
    private fun handleRequest(exchange: HttpExchange) {
        if ("GET" != exchange.requestMethod) {
            exchange.close()
        } else {
            val headers = exchange.requestHeaders
            val username = headers.getFirst("X-Minecraft-Username")
            val rawUuid = headers.getFirst("X-Minecraft-UUID")
            val clientVersion = headers.getFirst("X-Minecraft-Version")
            val clientVersionId = headers.getFirst("X-Minecraft-Version-ID")
            val rawPackFormat = headers.getFirst("X-Minecraft-Pack-Format")
            if (username != null && rawUuid != null && clientVersion != null && clientVersionId != null && rawPackFormat != null) {
                val uuid: UUID
                val packFormat: Int
                try {
                    uuid = UUID.randomUUID()
                    packFormat = rawPackFormat.toInt()
                } catch (var18: IllegalArgumentException) {
                    handler!!.onInvalidRequest(exchange)
                    exchange.close()
                    return
                }
                val request = ResourcePackRequest(uuid, username, clientVersion, clientVersionId, packFormat)
                try {
                    handler!!.onRequest(request, exchange)
                } catch (var16: Exception) {
                    handler!!.onException(var16)
                } finally {
                    exchange.close()
                }
            } else {
                handler!!.onInvalidRequest(exchange)
                exchange.close()
            }
        }
    }

    class Builder {

        private var address: InetSocketAddress? = null
        private var handler: ResourcePackWebServer? = null
        private var path = "/"
        fun address(address: InetSocketAddress): Builder {
            this.address = Objects.requireNonNull(address, "address") as InetSocketAddress
            return this
        }

        fun address(hostname: String?, port: Int): Builder {
            address = InetSocketAddress(hostname, port)
            return this
        }

        fun handler(handler: ResourcePackWebServer?): Builder {
            this.handler = handler
            return this
        }

        fun path(path: String): Builder {
            this.path = Objects.requireNonNull(path, "path") as String
            return this
        }

        @Throws(IOException::class)
        fun build(): ResourcePackServer {
            Objects.requireNonNull(address, "Address must be set!")
            Objects.requireNonNull(handler, "Handler must be set!")
            return ResourcePackServer(address, path, handler)
        }
    }

    companion object {

        fun builder(): Builder {
            return Builder()
        }
    }
}