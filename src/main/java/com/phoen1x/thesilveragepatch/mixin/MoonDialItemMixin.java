package com.phoen1x.thesilveragepatch.mixin;

import com.phantomwing.thesilverage.item.custom.MoonDialItem;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(MoonDialItem.class)
public class MoonDialItemMixin {
    @Inject(
        method = "appendTooltip",
        at = @At("HEAD"),
        cancellable = true
    )
    private void preventClientWorldCrashOnServer(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type, CallbackInfo ci) {
        ci.cancel();
    }
}