package com.phoen1x.thesilveragepatch.impl.item;

import com.phantomwing.thesilverage.utils.LevelUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.MinecraftServer;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import java.util.List;

public record MoonDialPolyItem(Item item) implements PolymerItem {

    private static final String[] PHASE_KEYS = new String[]{
            "thesilverage.moon_phase.full", "thesilverage.moon_phase.waning_gibbous",
            "thesilverage.moon_phase.third_quarter", "thesilverage.moon_phase.waning_crescent",
            "thesilverage.moon_phase.new", "thesilverage.moon_phase.waxing_crescent",
            "thesilverage.moon_phase.first_quarter", "thesilverage.moon_phase.waxing_gibbous"
    };

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.TRIAL_KEY;
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        MinecraftServer server = context.get(PacketContext.SERVER_INSTANCE);
        if (server != null) {
            var world = server.overworld();

            // Отримуємо значення, яке очікує модель (float)
            float phaseSignal = LevelUtils.getMoonPhaseSignal(world) / 16.0F;

            // Встановлюємо перший float в CustomModelData, який прочитає ванільний клієнт
            out.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(
                    List.of(phaseSignal),
                    List.of(),
                    List.of(),
                    List.of()
            ));

            // Бонус: додаємо Тултіп (Лор) з текстом фази місяця, оскільки ми відключили клієнтський!
            int phase = LevelUtils.getMoonPhase(world);
            if (phase >= 0 && phase < 8) {
                out.set(DataComponents.LORE, new ItemLore(List.of(
                        Component.translatable(PHASE_KEYS[phase]).withStyle(ChatFormatting.GRAY)
                )));
            }
        }
    }
}
