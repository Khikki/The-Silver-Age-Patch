package com.phoen1x.thesilveragepatch.mixin;

import com.phantomwing.thesilverage.ui.ModCreativeModeTab;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(ModCreativeModeTab.class)
public class ModCreativeModeTabMixin {
    @Redirect(
        method = "<clinit>", 
        at = @At(
            value = "INVOKE", 
            target = "Ldev/architectury/registry/registries/DeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;)Ldev/architectury/registry/registries/RegistrySupplier;"
        ),
        remap = false
    )
    private static RegistrySupplier<ItemGroup> polymerifyArchitecturyTab(DeferredRegister<ItemGroup> instance, String name, Supplier<ItemGroup> supplier) {
        Supplier<ItemGroup> wrappedSupplier = () -> {
            ItemGroup itemGroup = supplier.get();
            Identifier id = Identifier.of("thesilverage", name);
            PolymerItemGroupUtils.registerPolymerItemGroup(id, itemGroup);
            
            return itemGroup;
        };
        return instance.register(name, wrappedSupplier);
    }
}