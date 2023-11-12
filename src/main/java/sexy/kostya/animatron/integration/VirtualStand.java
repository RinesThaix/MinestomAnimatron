package sexy.kostya.animatron.integration;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.FramedPacket;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacketIdentifier;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.network.packet.server.play.EntityEquipmentPacket;
import net.minestom.server.network.packet.server.play.EntityTeleportPacket;
import net.minestom.server.network.packet.server.play.SpawnLivingEntityPacket;
import net.minestom.server.utils.PacketUtils;
import net.minestom.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

class VirtualStand {

    private final Entity  parent;
    final         int     id;
    private final UUID    uuid;
    final         boolean small;
    final         boolean visible;

    protected ItemStack helmet = ItemStack.AIR;
    private   Pos       position;
    private   Vec       headRotation;

    private final FramedPacket spawnPacket;
    private final FramedPacket despawnPacket;
    private       FramedPacket positionPacket;
    private       FramedPacket equipmentPacket;
    private       FramedPacket metadataPacket;

    public VirtualStand(Entity parent, boolean small, boolean visible) {
        this.parent = parent;
        this.id = Entity.generateId();
        this.uuid = UUID.randomUUID();
        this.small = !visible || small;
        this.visible = visible;

        this.position = parent.getPosition();
        this.headRotation = parent.getVelocity();

        this.spawnPacket = cachePacket(new SpawnLivingEntityPacket(
                id,
                uuid,
                EntityType.ARMOR_STAND.id(),
                position,
                position.yaw(),
                (short) 0, (short) 0, (short) 0
        ));
        this.despawnPacket = cachePacket(new DestroyEntitiesPacket(id));
        this.positionPacket = createPositionPacket(position);
        this.equipmentPacket = createEquipmentPacket();
        this.metadataPacket = createMetadataPacket();
    }

    public void show(Player player) {
        if (this.visible) {
            player.sendPackets(spawnPacket, metadataPacket, equipmentPacket, positionPacket);
        } else {
            player.sendPackets(spawnPacket, metadataPacket, positionPacket);
        }
    }

    public void hide(Player player) {
        player.sendPacket(despawnPacket);
    }

    public void updateHelmet(ItemStack helmet) {
        if (!this.visible) {
            return;
        }
        this.helmet = helmet;
        this.equipmentPacket = createEquipmentPacket();
        for (Player viewer : parent.getViewers()) {
            viewer.sendPacket(equipmentPacket);
        }
    }

    public void updatePosition(Pos pos) {
        if (pos.equals(this.position)) {
            return;
        }
        this.position = pos;
        this.positionPacket = createPositionPacket(pos);
        for (Player viewer : parent.getViewers()) {
            viewer.sendPacket(positionPacket);
        }
    }

    public void updateHeadRotation(Vec rot) {
        if (rot.equals(this.headRotation)) {
            return;
        }
        this.headRotation = rot;
        this.metadataPacket = createMetadataPacket();
        for (Player viewer : parent.getViewers()) {
            viewer.sendPacket(metadataPacket);
        }
    }

    private FramedPacket createPositionPacket(Pos pos) {
        return cachePacket(new EntityTeleportPacket(id, pos, false));
    }

    private FramedPacket createEquipmentPacket() {
        return cachePacket(new EntityEquipmentPacket(
                id,
                Map.of(EquipmentSlot.HELMET, helmet)
        ));
    }

    private FramedPacket createMetadataPacket() {
        return cachePacket(new MetadataPacket());
    }

    private static FramedPacket cachePacket(ServerPacket packet) {
        return PacketUtils.allocateTrimmedPacket(packet);
    }

    private final class MetadataPacket implements ServerPacket {

        @Override
        public void write(@NotNull BinaryWriter writer) {
            writer.writeVarInt(VirtualStand.this.id);

            // Invisible
            writer.writeByte((byte) 0);
            writer.writeVarInt(Metadata.TYPE_BYTE);
            writer.writeByte((byte) 0x20);

            // Silent
            writer.writeByte((byte) 4);
            writer.writeVarInt(Metadata.TYPE_BOOLEAN);
            writer.writeBoolean(true);

            // Has no gravity
            writer.writeByte((byte) 5);
            writer.writeVarInt(Metadata.TYPE_BOOLEAN);
            writer.writeBoolean(true);

            byte flags = 0;
            if (small) {
                // Small
                flags = 0x1;
            }
//            if (!visible) {
//                // Marker
//                flags |= 0x10;
//            }
            if (flags != 0) {
                writer.writeByte((byte) 15);
                writer.writeVarInt(Metadata.TYPE_BYTE);
                writer.writeByte(flags);
            }

            // Head rotation
            writer.writeByte((byte) 16);
            writer.writeVarInt(Metadata.TYPE_ROTATION);
            writer.writeFloat((float) headRotation.x());
            writer.writeFloat((float) headRotation.y());
            writer.writeFloat((float) headRotation.z());

            writer.writeByte((byte) 0xFF);
        }

        @Override
        public int getId() {
            return ServerPacketIdentifier.ENTITY_METADATA;
        }

    }

}
