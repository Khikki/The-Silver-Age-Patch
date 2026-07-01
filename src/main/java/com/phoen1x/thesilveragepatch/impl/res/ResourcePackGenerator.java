package com.phoen1x.thesilveragepatch.impl.res;

import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.factorytools.api.resourcepack.ModelModifiers;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.PackResource;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.phoen1x.thesilveragepatch.impl.TheSilverAgePatch.id;

public class ResourcePackGenerator {
    private static final Set<String> EXPANDABLE = Set.of("wall", "fence", "slab", "stairs", "pressure_plate", "button");

    public static void setup() {
        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(ResourcePackGenerator::build);
    }

    private static void build(ResourcePackBuilder builder) {
        final var expansion = new Vec3(0.08, 0.08, 0.08);
        var atlas = AtlasAsset.builder();

        builder.forEachResource((string, resource) -> {
            var bytes = resource.readAllBytes();
            for (var expandable : EXPANDABLE) {
                if (string.contains(expandable) && string.startsWith("assets/thesilverage/models/block/")) {
                    var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                    if (asset.parent().isPresent()) {
                        var parentId = asset.parent().get();
                        var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));
                        builder.addData(AssetPaths.model("thesilveragepatch", parentId.getPath()) + ".json", ModelModifiers.expandModel(parentAsset, expansion));
                    }
                }
            }
        });

        for (var entry : BlockStateModelManager.UV_LOCKED_MODELS.get("thesilverage").entrySet()) {
            var expand = EXPANDABLE.stream().anyMatch(expandable -> entry.getKey().contains(expandable) && entry.getKey().startsWith("block/")) ? expansion : Vec3.ZERO;
            for (var v : entry.getValue()) {
                var suffix = "_uvlock_" + v.x() + "_" + v.y();
                var modelId = v.model().withSuffix(suffix);
                var asset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getData(AssetPaths.model(v.model()) + ".json")), StandardCharsets.UTF_8));

                if (asset.parent().isPresent()) {
                    var parentId = asset.parent().get();
                    var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));
                    builder.addData(AssetPaths.model("thesilveragepatch", parentId.getPath() + suffix) + ".json",
                            ModelModifiers.expandModelAndRotateUVLocked(parentAsset, expand, v.x(), v.y()));
                    builder.addData(AssetPaths.model(modelId) + ".json",
                            new ModelAsset(Optional.of(Identifier.fromNamespaceAndPath("thesilveragepatch", parentId.getPath() + suffix)), asset.elements(),
                                    asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes());
                }
            }
        }

        builder.addResourceConverter(((string, resource) -> {
            // ФІКС ДЛЯ MOON DIAL: Ванільний клієнт не розуміє "thesilverage:moon_phase"
            // Замінюємо його на ванільне "minecraft:custom_model_data" прямо в згенерованому JSON
            if (string.endsWith("moon_dial.json")) {
                String json = resource.asString();
                if (json.contains("\"thesilverage:moon_phase\"")) {
                    json = json.replace("\"thesilverage:moon_phase\"", "\"minecraft:custom_model_data\"");
                    return PackResource.of(json.getBytes(StandardCharsets.UTF_8));
                }
            }

            if (!string.contains("_uvlock_")) {
                for (var expandable : EXPANDABLE) {
                    if (string.contains(expandable) && string.startsWith("assets/thesilverage/models/block/")) {
                        var asset = ModelAsset.fromJson(resource.asString());
                        return PackResource.fromAsset(new ModelAsset(asset.parent().map(x -> id(x.getPath())), asset.elements(), asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()));
                    }
                }
            }

            return resource;
        }));

        builder.addData("assets/minecraft/atlases/blocks.json", atlas.build());
    }
}
