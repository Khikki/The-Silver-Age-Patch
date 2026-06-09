package com.phoen1x.thesilveragepatch.impl.block;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.BlockStateAsset;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PolymerBlockHelper {
    public static BlockState requestPolymerBlockState(Identifier id, String variant, BlockModelType blockModelType) throws IOException {
        var modContainer = FabricLoader.getInstance().getModContainer(id.getNamespace())
                .orElseThrow(() -> new IOException("Mod not found: " + id.getNamespace()));

        Path path = modContainer.findPath("assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json")
                .orElseThrow(() -> new IOException("Blockstate file not found: " + id));

        String content = Files.readString(path);
        var json = JsonParser.parseString(content);

        BlockStateAsset asset = BlockStateAsset.CODEC.decode(JsonOps.INSTANCE, json)
                .getOrThrow().getFirst();

        var models = asset.variants().get().get(variant);

        return PolymerBlockResourceUtils.requestBlock(
                blockModelType,
                models.stream().map(x -> new PolymerBlockModel(x.model(), x.x(), x.y(), x.uvlock(), x.weigth()))
                        .toArray(PolymerBlockModel[]::new)
        );
    }
}