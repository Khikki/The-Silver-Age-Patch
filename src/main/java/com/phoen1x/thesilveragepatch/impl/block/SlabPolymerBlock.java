package com.phoen1x.thesilveragepatch.impl.block;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.ShiftyBlockStateModel;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.io.IOException;

public record SlabPolymerBlock(BlockState bottomState, BlockState bottomStateWaterlogged) implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {

    private static final BlockState[] EMPTY_STATES = new BlockState[4];

    static {
        EMPTY_STATES[0] = PolymerBlockResourceUtils.requestEmpty(BlockModelType.TOP_SLAB);
        EMPTY_STATES[1] = PolymerBlockResourceUtils.requestEmpty(BlockModelType.TOP_SLAB_WATERLOGGED);
        EMPTY_STATES[2] = PolymerBlockResourceUtils.requestEmpty(BlockModelType.BOTTOM_SLAB);
        EMPTY_STATES[3] = PolymerBlockResourceUtils.requestEmpty(BlockModelType.BOTTOM_SLAB_WATERLOGGED);
    }

    public static SlabPolymerBlock of(Block block, Identifier id) {
        try {
            BlockState bottom = PolymerBlockHelper.requestPolymerBlockState(id, "type=bottom", BlockModelType.SCULK_SENSOR_BLOCK);
            BlockState bottomWaterlogged = PolymerBlockHelper.requestPolymerBlockState(id, "type=bottom", BlockModelType.SCULK_SENSOR_BLOCK_WATERLOGGED);
            return new SlabPolymerBlock(bottom, bottomWaterlogged);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        SlabType slabType = state.get(SlabBlock.TYPE);
        boolean waterlogged = state.get(SlabBlock.WATERLOGGED);

        if (slabType == SlabType.BOTTOM) {
            BlockState vanillaState = waterlogged ? bottomStateWaterlogged : bottomState;
            if (vanillaState != null) return vanillaState;
        }

        if (slabType == SlabType.DOUBLE) {
            return Blocks.BARRIER.getDefaultState().with(Properties.WATERLOGGED, waterlogged);
        } else {
            int i = (slabType == SlabType.TOP) ? 0 : 2;
            if (waterlogged) i++;
            return EMPTY_STATES[i];
        }
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        SlabType slabType = initialBlockState.get(SlabBlock.TYPE);
        BlockState vanillaState = initialBlockState.get(SlabBlock.WATERLOGGED) ? bottomStateWaterlogged : bottomState;
        if (slabType == SlabType.BOTTOM && vanillaState != null) return null;
        return ShiftyBlockStateModel.midRange(initialBlockState, pos);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult blockHitResult) {
        return true;
    }
}