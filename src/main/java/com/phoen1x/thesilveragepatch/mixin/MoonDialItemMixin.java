package com.phoen1x.thesilveragepatch.mixin;

import com.phantomwing.thesilverage.item.custom.MoonDialItem;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(MoonDialItem.class)
public class MoonDialItemMixin {
    @Inject(
        method = "appendHoverText",
        at = @At("HEAD"),
        cancellable = true
    )
    private void preventClientWorldCrashOnServer(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type, CallbackInfo ci) {
        ci.cancel();
    }
}
