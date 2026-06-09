package com.phoen1x.thesilveragepatch.mixin;

import com.phantomwing.thesilverage.block.ModBlocks;
import com.phantomwing.thesilverage.block.custom.MoonPhaseDetectorBlock;
import com.phoen1x.thesilveragepatch.impl.TheSilverAgePatch;
import com.phoen1x.thesilveragepatch.impl.block.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mixin(ModBlocks.class)
public class ModBlocksMixin {
    @Redirect(
            method = "registerSilverBlock(Ljava/lang/String;Lnet/minecraft/block/AbstractBlock$Settings;Ljava/util/function/Function;)Ldev/architectury/registry/registries/RegistrySupplier;",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/architectury/registry/registries/DeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;)Ldev/architectury/registry/registries/RegistrySupplier;"
            ),
            remap = false
    )
    private static RegistrySupplier<Block> polymerifyBlocks(DeferredRegister<Block> instance, String name, Supplier<Block> supplier) {
        return instance.register(name, () -> {
            Block block = supplier.get();
            TheSilverAgePatch.LATE_INIT.add(() -> BlockStateModelManager.addBlock(Registries.BLOCK.getId(block), block));
            PolymerBlock overlay = null;
            Identifier location = Identifier.of("thesilverage", name);

            if (block instanceof DoorBlock) {
                overlay = DoorPolymerBlock.INSTANCE;
            } else if (block instanceof TrapdoorBlock) {
                overlay = TrapdoorPolymerBlock.INSTANCE;
            } else if (block instanceof SlabBlock) {
                overlay = (PolymerBlock) (Object) SlabPolymerBlock.of(block, location);
            } else if (block instanceof StairsBlock) {
                overlay = StateCopyFactoryBlock.STAIR;
            } else if (block instanceof MoonPhaseDetectorBlock) {
                overlay = BaseFactoryBlock.TRIPWIRE;
            }

            if (overlay == null) {
                if (block.getDefaultState().getCollisionShape(PolymerCommonUtils.getFakeWorld(), BlockPos.ORIGIN).isEmpty()) {
                    overlay = BaseFactoryBlock.SAPLING;
                } else {
                    overlay = BaseFactoryBlock.BARRIER;
                }
            }

            PolymerBlock.registerOverlay(block, overlay);
            if (overlay instanceof BlockWithElementHolder blockWithElementHolder) {
                BlockWithElementHolder.registerOverlay(block, blockWithElementHolder);
            }
            return block;
        });
    }
}