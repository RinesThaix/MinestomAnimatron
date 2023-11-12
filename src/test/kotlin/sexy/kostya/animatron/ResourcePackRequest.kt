package sexy.kostya.animatron

import java.util.UUID
import net.kyori.examination.Examinable
import java.util.Objects
import net.kyori.examination.ExaminableProperty
import net.kyori.examination.string.StringExaminer
import sexy.kostya.animatron.ResourcePackRequest
import java.util.stream.Stream

class ResourcePackRequest(
    uuid: UUID,
    username: String,
    clientVersion: String,
    clientVersionId: String,
    packFormat: Int
) : Examinable {

    private val uuid: UUID
    private val username: String
    private val clientVersion: String
    private val clientVersionId: String
    private val packFormat: Int

    init {
        this.uuid = Objects.requireNonNull(uuid, "uuid") as UUID
        this.username = Objects.requireNonNull(username, "username") as String
        this.clientVersion = Objects.requireNonNull(clientVersion, "clientVersion") as String
        this.clientVersionId = Objects.requireNonNull(clientVersionId, "clientVersionId") as String
        this.packFormat = packFormat
    }

    fun uuid(): UUID {
        return uuid
    }

    fun username(): String {
        return username
    }

    fun clientVersion(): String {
        return clientVersion
    }

    fun clientVersionId(): String {
        return clientVersionId
    }

    fun packFormat(): Int {
        return packFormat
    }

    override fun examinableProperties(): Stream<out ExaminableProperty> {
        return Stream.of(
            ExaminableProperty.of("uuid", uuid),
            ExaminableProperty.of("username", username),
            ExaminableProperty.of("clientVersion", clientVersion),
            ExaminableProperty.of("clientVersionId", clientVersionId),
            ExaminableProperty.of("packFormat", packFormat)
        )
    }

    override fun toString(): String {
        return examine(StringExaminer.simpleEscaping())
    }

    override fun equals(o: Any?): Boolean {
        return if (this === o) {
            true
        } else if (o != null && this.javaClass == o.javaClass) {
            val that = o as ResourcePackRequest
            packFormat == that.packFormat && uuid == that.uuid && username == that.username && clientVersion == that.clientVersion && clientVersionId == that.clientVersionId
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(*arrayOf<Any>(uuid, username, clientVersion, clientVersionId, packFormat))
    }
}