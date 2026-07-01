package com.phoen1x.thesilveragepatch.impl.block;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.ShiftyBlockStateModel;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import java.io.IOException;

public record SlabPolymerBlock(BlockState bottomState, BlockState bottomStateWaterlogged) implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {

    private static final BlockState[] EMPTY_STATES = new BlockState[4];

    static {
        EMPTY_STATES[0] = PolymerBlockResourceUtils.requestEmpty(BlockModelType.SLAB_TOP);
        EMPTY_STATES[1] = PolymerBlockResourceUtils.requestEmpty(BlockModelType.SLAB_TOP_WATERLOGGED);
        EMPTY_STATES[2] = PolymerBlockResourceUtils.requestEmpty(BlockModelType.SLAB_BOTTOM);
        EMPTY_STATES[3] = PolymerBlockResourceUtils.requestEmpty(BlockModelType.SLAB_BOTTOM_WATERLOGGED);
    }

    public static SlabPolymerBlock of(Block block, Identifier id) {
        try {
            BlockState bottom = PolymerBlockHelper.requestPolymerBlockState(id, "type=bottom", BlockModelType.SCULK_SENSOR);
            BlockState bottomWaterlogged = PolymerBlockHelper.requestPolymerBlockState(id, "type=bottom", BlockModelType.SCULK_SENSOR_WATERLOGGED);
            return new SlabPolymerBlock(bottom, bottomWaterlogged);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        SlabType slabType = state.getValue(SlabBlock.TYPE);
        boolean waterlogged = state.getValue(SlabBlock.WATERLOGGED);

        if (slabType == SlabType.BOTTOM) {
            BlockState vanillaState = waterlogged ? bottomStateWaterlogged : bottomState;
            if (vanillaState != null) return vanillaState;
        }

        if (slabType == SlabType.DOUBLE) {
            return Blocks.BARRIER.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, waterlogged);
        } else {
            int i = (slabType == SlabType.TOP) ? 0 : 2;
            if (waterlogged) i++;
            return EMPTY_STATES[i];
        }
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        SlabType slabType = initialBlockState.getValue(SlabBlock.TYPE);
        BlockState vanillaState = initialBlockState.getValue(SlabBlock.WATERLOGGED) ? bottomStateWaterlogged : bottomState;
        if (slabType == SlabType.BOTTOM && vanillaState != null) return null;
        return ShiftyBlockStateModel.midRange(initialBlockState, pos);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult) {
        return true;
    }
}
