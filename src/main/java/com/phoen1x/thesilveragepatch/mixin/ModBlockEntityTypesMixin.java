package com.phoen1x.thesilveragepatch.mixin;

import com.phantomwing.thesilverage.block.ModBlockEntityTypes;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(ModBlockEntityTypes.class)
public class ModBlockEntityTypesMixin {
    @Redirect(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Ldev/architectury/registry/registries/DeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;)Ldev/architectury/registry/registries/RegistrySupplier;"
        ),
        remap = false
    )
    private static RegistrySupplier<BlockEntityType<?>> polymerifyBlockEntity(DeferredRegister<BlockEntityType<?>> instance, String name, Supplier<BlockEntityType<?>> supplier) {
        return instance.register(name, () -> {
            BlockEntityType<?> blockEntity = supplier.get();
            PolymerBlockUtils.registerBlockEntity(blockEntity);
            return blockEntity;
        });
    }
}