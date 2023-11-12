package sexy.kostya.animatron

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.Chunk
import net.minestom.server.instance.ChunkGenerator
import net.minestom.server.instance.ChunkPopulator
import net.minestom.server.instance.batch.ChunkBatch
import net.minestom.server.instance.block.Block

fun main() {
    val server = MinecraftServer.init()
    val instance = MinecraftServer.getInstanceManager().createInstanceContainer()

    instance.setChunkGenerator(object : ChunkGenerator {
        override fun generateChunkData(batch: ChunkBatch, chunkX: Int, chunkZ: Int) {
            for (x in 0 until Chunk.CHUNK_SIZE_X)
                for (z in 0 until Chunk.CHUNK_SIZE_Z)
                    batch.setBlock(x, 70, z, Block.GRASS_BLOCK)
        }

        override fun getPopulators(): List<ChunkPopulator>? {
            return null
        }
    })
    val eventHandler = MinecraftServer.getGlobalEventHandler()
    eventHandler.addListener(
        PlayerLoginEvent::class.java
    ) { event: PlayerLoginEvent ->
        val player = event.player
        player.gameMode = GameMode.CREATIVE
        event.setSpawningInstance(instance)
        player.respawnPoint = Pos(0.0, 72.0, 0.0)
    }

    val resourcePack = ModelEngine.generate("test", true)!!
    val provider = ResourcePackWebServer.of(resourcePack)
    val webServer = ResourcePackServer.builder()
        .address("127.0.0.1", 7272)
        .handler(provider)
        .build()

    val registry = ModelEngine.registry()
    MinecraftServer.LOGGER.info("Registered models: ${registry.registeredModels}.")

    MinecraftServer.getCommandManager().register(TestCommand())

    webServer.start()
    server.start("127.0.0.1", 25565)
    MinecraftServer.getSchedulerManager().buildShutdownTask { webServer.stop(10) }
}