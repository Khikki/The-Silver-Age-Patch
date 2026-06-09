package com.phoen1x.thesilveragepatch.impl.item;

import com.phantomwing.thesilverage.utils.LevelUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.packettweaker.PacketContext;

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
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context) {
        var player = context.getPlayer();
        if (player != null) {
            var world = player.getWorld();

            // Отримуємо значення, яке очікує модель (float)
            float phaseSignal = LevelUtils.getMoonPhaseSignal(world) / 16.0F;

            // Встановлюємо перший float в CustomModelData, який прочитає ванільний клієнт
            out.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(
                    List.of(phaseSignal),
                    List.of(),
                    List.of(),
                    List.of()
            ));

            // Бонус: додаємо Тултіп (Лор) з текстом фази місяця, оскільки ми відключили клієнтський!
            int phase = LevelUtils.getMoonPhase(world);
            if (phase >= 0 && phase < 8) {
                out.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                        Text.translatable(PHASE_KEYS[phase]).formatted(Formatting.GRAY)
                )));
            }
        }
    }
}