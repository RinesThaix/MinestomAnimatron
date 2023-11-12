package sexy.kostya.animatron.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class Bone {

    private final String            parent;
    private final String            name;
    private final double[]          globalOffset  = new double[3];
    private final double[]          localOffset   = new double[3];
    private final double[]          localRotation = new double[3];
    private       int               customModelData;
    private final Set<Option>       options       = EnumSet.noneOf(Option.class);
    private final Map<String, Bone> child         = new TreeMap<>();

    public Bone(String parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public double getLocalOffsetX() {
        return this.localOffset[0];
    }

    public double getLocalOffsetY() {
        return this.localOffset[1];
    }

    public double getLocalOffsetZ() {
        return -this.localOffset[2];
    }

    public double getLocalRotationX() {
        return -Math.toRadians(this.localRotation[0]);
    }

    public double getLocalRotationY() {
        return -Math.toRadians(this.localRotation[1]);
    }

    public double getLocalRotationZ() {
        return Math.toRadians(this.localRotation[2]);
    }

    public void setGlobalOffset(final double x, final double y, final double z) {
        this.globalOffset[0] = x;
        this.globalOffset[1] = y;
        this.globalOffset[2] = z;
    }

    public void setRelativeOffset(final double x, final double y, final double z) {
        this.setLocalOffset(this.globalOffset[0] - x, this.globalOffset[1] - y, this.globalOffset[2] - z);
    }

    public void setLocalOffset(final double x, final double y, final double z) {
        this.localOffset[0] = x;
        this.localOffset[1] = y;
        this.localOffset[2] = z;
    }

    public void setLocalRotation(final double x, final double y, final double z) {
        this.localRotation[0] = x;
        this.localRotation[1] = y;
        this.localRotation[2] = z;
    }

    public void updateChildRelativeOffset() {
        if (this.child.isEmpty()) {
            return;
        }
        for (final String bone : this.child.keySet()) {
            this.child.get(bone).setRelativeOffset(this.globalOffset[0], this.globalOffset[1], this.globalOffset[2]);
            this.child.get(bone).updateChildRelativeOffset();
        }
    }

    public void addChild(final String boneId, final Bone bone) {
        this.child.put(boneId.toLowerCase(), bone);
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public boolean hasOption(Option option) {
        return options.contains(option);
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public Map<String, Bone> getChild() {
        return child;
    }

    public Bone getBone(final String boneId) {
        if (this.child.containsKey(boneId)) {
            return this.child.get(boneId);
        }
        for (final String bId : this.child.keySet()) {
            final Bone b = this.child.get(bId).getBone(boneId);
            if (b != null) {
                return b;
            }
        }
        return null;
    }

    public enum Option {
        SMALL,
        NAME_TAG,
        DRIVER_SEAT,
        PASSENGER_SEAT,
        ITEM_RIGHT,
        ITEM_LEFT,
        HEAD,
        SEGMENT
    }

}
