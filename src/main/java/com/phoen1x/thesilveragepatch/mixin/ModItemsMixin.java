package com.phoen1x.thesilveragepatch.mixin;

import com.phantomwing.thesilverage.item.ModItems;
import com.phoen1x.thesilveragepatch.impl.item.MoonDialPolyItem;
import com.phoen1x.thesilveragepatch.impl.item.PolyBaseItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(ModItems.class)
public class ModItemsMixin {
    @Redirect(
            method = {
                    "registerKnife",
                    "registerWithModCompat",
                    "register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Ldev/architectury/registry/registries/RegistrySupplier;"
            },
            at = @At(value = "INVOKE",
                    target = "Ldev/architectury/registry/registries/DeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;)Ldev/architectury/registry/registries/RegistrySupplier;"
            ),
            remap = false
    )
    private static RegistrySupplier<Item> polymerifyItems(DeferredRegister<Item> instance, String name, Supplier<Item> supplier) {
        return instance.register(name, () -> {
            Item registeredItem = supplier.get();
            PolymerItem polymerItem;

            if (name.equals("moon_dial")) {
                polymerItem = new MoonDialPolyItem(registeredItem);
            } else {
                polymerItem = new PolyBaseItem(registeredItem);
            }

            PolymerItem.registerOverlay(registeredItem, polymerItem);
            return registeredItem;
        });
    }
}
