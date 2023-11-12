package sexy.kostya.animatron.integration;

import net.minestom.server.color.Color;
import net.minestom.server.entity.Entity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.LeatherArmorMeta;
import sexy.kostya.animatron.model.Bone;

public class VirtualBone extends VirtualStand {

    private final static Color RED_COLOR   = new Color(0xFF7777);
    private final static Color WHITE_COLOR = new Color(0xFFFFFF);

    private final int customModelData;

    public VirtualBone(Entity parent, Bone bone) {
        super(parent, bone.hasOption(Bone.Option.SMALL), bone.getCustomModelData() != 0);
        this.customModelData = bone.getCustomModelData();
    }

    public void startDamage() {
        colorize(RED_COLOR);
    }

    public void endDamage() {
        colorize(WHITE_COLOR);
    }

    public void colorize(int rgb) {
        colorize(new Color(rgb));
    }

    public void colorize(int r, int g, int b) {
        colorize(new Color(r, g, b));
    }

    public void colorize(Color color) {
        if (!visible) {
            return;
        }
        if (!super.helmet.isAir()) {
            final LeatherArmorMeta meta = super.helmet.meta(LeatherArmorMeta.class);
            if (color.equals(meta.getColor())) {
                return;
            }
        }
        updateHelmet(
                ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
                        .meta(LeatherArmorMeta.class, meta -> meta.color(color).customModelData(customModelData))
                        .build()
        );
    }

}
