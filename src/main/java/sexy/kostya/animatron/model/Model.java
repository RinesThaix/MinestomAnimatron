package sexy.kostya.animatron.model;

import sexy.kostya.animatron.animation.Animation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Model {

    private final float[]                boundingBox;
    private final Map<String, Float[]>   subBoundingBox;
    private       float                  eyeHeight;
    private final Map<String, Integer>   customModelDatas;
    private final Map<String, Bone>      bones;
    private final Map<String, Animation> animations;

    public Model() {
        this.boundingBox = new float[]{0.0f, 0.0f};
        this.subBoundingBox = new LinkedHashMap<>();
        this.eyeHeight = 0.0f;
        this.customModelDatas = new LinkedHashMap<>();
        this.bones = new LinkedHashMap<>();
        this.animations = new LinkedHashMap<>();
    }

    public void setBoundingBox(final float width, final float height) {
        if (width == 0.0f && height == 0.0f) {
            throw new IllegalArgumentException("Empty bounding box");
        }
        this.boundingBox[0] = width / 16.0f;
        this.boundingBox[1] = height / 16.0f;
    }

    public void setSubBoundingBox(final String boneId, final float width, final float height, final float depth) {
        if (width == 0.0f && height == 0.0f && depth == 0.0f) {
            return;
        }
        this.subBoundingBox.put(boneId, new Float[]{width / 16.0f, height / 16.0f, depth / 16.0f});
    }

    public void setEyeHeight(final float height) {
        if (height <= 0.0f) {
            throw new IllegalArgumentException("Negative eye height");
        }
        this.eyeHeight = height / 16.0f;
    }

    public void addItemModelID(final String partName, final int id) {
        this.customModelDatas.put(partName, id);
    }

    public void reassignId() {
        for (final String partId : this.customModelDatas.keySet()) {
            this.getBone(partId).setCustomModelData(this.customModelDatas.get(partId));
        }
    }

    public void addBone(final String boneId, final Bone bone) {
        this.bones.put(boneId.toLowerCase(), bone);
    }

    public void setBones(final Map<String, Bone> bones) {
        this.bones.clear();
        this.bones.putAll(bones);
    }

    public void addAnimation(final String state, final Animation animation) {
        this.animations.put(state, animation);
    }

    public void setAnimation(final Map<String, Animation> animations) {
        this.animations.clear();
        this.animations.putAll(animations);
    }

    public float getBoundingBoxWidth() {
        return this.boundingBox[0];
    }

    public float getBoundingBoxHeight() {
        return this.boundingBox[1];
    }

    public float getSubBoundingBoxWidth(final String boneId) {
        return this.subBoundingBox.containsKey(boneId) ? this.subBoundingBox.get(boneId)[0] : 0.0f;
    }

    public float getSubBoundingBoxHeight(final String boneId) {
        return this.subBoundingBox.containsKey(boneId) ? this.subBoundingBox.get(boneId)[1] : 0.0f;
    }

    public float getSubBoundingBoxDepth(final String boneId) {
        return this.subBoundingBox.containsKey(boneId) ? this.subBoundingBox.get(boneId)[2] : 0.0f;
    }

    public float getEyeHeight() {
        return this.eyeHeight;
    }

    public int getItemId(final String boneId) {
        return this.customModelDatas.getOrDefault(boneId, -1);
    }

    public Map<String, Integer> getItemMap() {
        return this.customModelDatas;
    }

    public Bone getBone(final String boneId) {
        if (this.bones.containsKey(boneId)) {
            return this.bones.get(boneId);
        }
        for (final String bId : this.bones.keySet()) {
            final Bone b = this.bones.get(bId).getBone(boneId);
            if (b != null) {
                return b;
            }
        }
        return null;
    }

    public Map<String, Bone> getBones() {
        return this.bones;
    }

    public List<String> getAllBoneIds() {
        final List<String> ids = new ArrayList<String>(this.bones.keySet());
        for (final Bone child : this.bones.values()) {
            this.getAllBoneIds0(ids, child);
        }
        return ids;
    }

    private void getAllBoneIds0(final List<String> ids, final Bone bone) {
        ids.addAll(bone.getChild().keySet());
        for (final Bone child : bone.getChild().values()) {
            this.getAllBoneIds0(ids, child);
        }
    }

    public Animation getAnimation(final String state) {
        return this.animations.get(state);
    }

    public Map<String, Animation> getAnimations() {
        return this.animations;
    }

}
