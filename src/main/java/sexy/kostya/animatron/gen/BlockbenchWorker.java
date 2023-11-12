package sexy.kostya.animatron.gen;

import sexy.kostya.animatron.gen.base.ModelGenerator;
import sexy.kostya.animatron.gen.component.resourcepack.JavaElement;
import sexy.kostya.animatron.gen.component.resourcepack.element.Cube;
import sexy.kostya.animatron.gen.component.resourcepack.element.Face;
import sexy.kostya.animatron.gen.component.resourcepack.element.Rotation;
import sexy.kostya.animatron.gen.element.*;
import sexy.kostya.animatron.model.Bone;

import java.util.*;

public class BlockbenchWorker {

    private final int                  tWidth;
    private final int                  tHeight;
    private       float                width;
    private       float                height;
    private       float                eyeHeight;
    private final List<JavaElement>    parts;
    private final Map<String, Bone>    bones;
    private final Map<String, Float[]> subBoundingBox;
    private final ModelGenerator       generator;
    private final BlockbenchModel      model;

    public BlockbenchWorker(final ModelGenerator generator, final BlockbenchModel model) {
        this.width = 0.0f;
        this.height = 0.0f;
        this.eyeHeight = 0.0f;
        this.parts = new ArrayList<JavaElement>();
        this.bones = new TreeMap<String, Bone>();
        this.subBoundingBox = new TreeMap<String, Float[]>();
        this.generator = generator;
        this.model = model;
        this.tWidth = model.width;
        this.tHeight = model.height;
        for (final BBChild child : model.elements) {
            if (child instanceof BBOutliner) {
                final BBOutliner outliner = (BBOutliner) child;
                final Bone       bone     = this.createBone(null, outliner);
                if (bone == null) {
                    continue;
                }
                this.bones.put(outliner.name.toLowerCase(), bone);
            }
        }
        for (final String bone2 : this.bones.keySet()) {
            this.bones.get(bone2).setRelativeOffset(0.0, 0.0, 0.0);
            this.bones.get(bone2).updateChildRelativeOffset();
        }
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public Map<String, Float[]> getSubBoundingBox() {
        return this.subBoundingBox;
    }

    public float getEyeHeight() {
        return this.eyeHeight;
    }

    public List<JavaElement> getParts() {
        return this.parts;
    }

    public Map<String, Bone> getBones() {
        return this.bones;
    }

    private Bone createBone(final String parent, final BBOutliner outliner) {
        final JavaElement part    = new JavaElement();
        boolean           hasCube = false;
        if (outliner.name.equalsIgnoreCase("hitbox")) {
            this.eyeHeight = outliner.origin[1];
            for (final BBChild child : outliner.children) {
                if (child instanceof BBCube) {
                    final BBCube bbCube = (BBCube) child;
                    this.width = bbCube.to[0] - bbCube.from[0];
                    this.height = bbCube.to[1] - bbCube.from[1];
                    break;
                }
            }
            return null;
        }
        final Bone   bone      = configureBone(parent, outliner.name);
        final String newPartId = bone.getName();
        bone.setGlobalOffset(-outliner.origin[0] / 16.0f, outliner.origin[1] / 16.0f, outliner.origin[2] / 16.0f);
        bone.setLocalRotation(outliner.rotation[0], outliner.rotation[1], outliner.rotation[2]);
        if (outliner.children.size() == 1 && outliner.children.get(0) instanceof BBCube) {
            if (outliner.name.toLowerCase().startsWith("b_")) {
                final BBCube cube   = (BBCube) outliner.children.get(0);
                final float  width  = cube.to[0] - cube.from[0];
                final float  height = cube.to[1] - cube.from[1];
                this.subBoundingBox.put(outliner.name, new Float[]{width, height, width});
                bone.setGlobalOffset(-0.03125 * (cube.to[0] + cube.from[0]), 0.03125 * (cube.to[1] + cube.from[1]), 0.03125 * (cube.to[2] + cube.from[2]));
                return bone;
            }
            if (outliner.name.toLowerCase().startsWith("ob_")) {
                final BBCube cube   = (BBCube) outliner.children.get(0);
                final float  width  = cube.to[0] - cube.from[0];
                final float  height = cube.to[1] - cube.from[1];
                final float  depth  = cube.to[2] - cube.from[2];
                this.subBoundingBox.put(outliner.name, new Float[]{width, height, depth});
                bone.setGlobalOffset(-0.03125 * (cube.to[0] + cube.from[0]), 0.03125 * (cube.to[1] + cube.from[1]), 0.03125 * (cube.to[2] + cube.from[2]));
                return bone;
            }
        }
        for (final BBChild child2 : outliner.children) {
            if (child2 instanceof BBOutliner) {
                final BBOutliner cOut  = (BBOutliner) child2;
                final Bone       cBone = this.createBone(newPartId, cOut);
                if (cBone == null) {
                    continue;
                }
                cBone.setRelativeOffset(outliner.origin[0], outliner.origin[1], outliner.origin[2]);
                bone.addChild(cBone.getName(), cBone);
            } else {
                if (!(child2 instanceof BBCube)) {
                    continue;
                }
                final BBCube bbCube2 = (BBCube) child2;
                final Cube   cube2   = this.createCube(bbCube2, outliner);
                part.addElements(cube2);
                hasCube = true;
            }
        }
        if (hasCube) {
            part.setFileName(newPartId);
            part.setTextureSize(this.tWidth, this.tHeight);
            if (part.normalize(newPartId)) {
                bone.addOption(Bone.Option.SMALL);
            }
            for (final BBTexture texture : this.model.textures) {
                if (texture.namespace.isEmpty()) {
                    texture.namespace = this.generator.getNamespace();
                    texture.folder = "entity";
                } else if (!texture.namespace.equals("minecraft")) {
                    texture.folder = "entity";
                }
                part.addTexture(texture.id, String.format("%s:%s/%s", texture.namespace, texture.folder, texture.name));
            }
            this.parts.add(part);
        }
        return bone;
    }

    private Bone configureBone(final String parentBoneName, final String boneName) {

        Set<Bone.Option> options = EnumSet.noneOf(Bone.Option.class);

        if (boneName.startsWith("tag_")) {
            options.add(Bone.Option.NAME_TAG);
        } else if (boneName.equals("mount")) {
            options.add(Bone.Option.DRIVER_SEAT);
        } else if (boneName.startsWith("p_")) {
            options.add(Bone.Option.PASSENGER_SEAT);
        } else if (boneName.startsWith("ir_")) {
            options.add(Bone.Option.ITEM_RIGHT);
        } else if (boneName.startsWith("il_")) {
            options.add(Bone.Option.ITEM_LEFT);
        } else if (boneName.startsWith("h_")) {
            options.add(Bone.Option.HEAD);
        } else if (boneName.startsWith("seg_")) {
            options.add(Bone.Option.SEGMENT);
        }

        final Bone bone = new Bone(parentBoneName, boneName);
        for (Bone.Option option : options) {
            bone.addOption(option);
        }
        return bone;
    }

    private Cube createCube(final BBCube bbcube, final BBOutliner bone) {
        final Cube cube = new Cube();
        cube.setName(bbcube.name.toLowerCase());
        final float fX = bbcube.from[0] - bone.origin[0] - bbcube.inflate;
        final float fY = bbcube.from[1] - bone.origin[1] - bbcube.inflate;
        final float fZ = bbcube.from[2] - bone.origin[2] - bbcube.inflate;
        cube.setFrom(fX + 8.0f, fY + 8.0f, fZ + 8.0f);
        final float tX = bbcube.to[0] - bone.origin[0] + bbcube.inflate;
        final float tY = bbcube.to[1] - bone.origin[1] + bbcube.inflate;
        final float tZ = bbcube.to[2] - bone.origin[2] + bbcube.inflate;
        cube.setTo(tX + 8.0f, tY + 8.0f, tZ + 8.0f);
        final Rotation rotation = new Rotation();
        final String   axis     = bbcube.getAxis(bone.name);
        switch (axis) {
            case "x": {
                rotation.setAxis("x");
                rotation.setAngle(bbcube.rotation[0], bbcube.name, bone.name);
                break;
            }
            case "y": {
                rotation.setAxis("y");
                rotation.setAngle(bbcube.rotation[1], bbcube.name, bone.name);
                break;
            }
            case "z": {
                rotation.setAxis("z");
                rotation.setAngle(bbcube.rotation[2], bbcube.name, bone.name);
                break;
            }
        }
        rotation.setOrigin(bbcube.origin[0] - bone.origin[0] + 8.0f, bbcube.origin[1] - bone.origin[1] + 8.0f, bbcube.origin[2] - bone.origin[2] + 8.0f);
        cube.setRotation(rotation);
        final float wRatio = 16.0f / this.tWidth;
        final float hRatio = 16.0f / this.tHeight;
        for (final String side : bbcube.faces.keySet()) {
            final BBFace face  = bbcube.faces.get(side);
            final Face   cFace = new Face();
            cFace.setUV(face.uv[0] * wRatio, face.uv[1] * hRatio, face.uv[2] * wRatio, face.uv[3] * hRatio);
            cFace.setTexture("#" + this.model.textures.get(face.texture).id);
            cFace.setRotation(face.rotation);
            cube.addFace(side, cFace);
        }
        return cube;
    }

}
