package com.elysianrealm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = ElysianRealm.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ElysianPortalEventHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();

        if (stack.getItem() instanceof FlintAndSteelItem && level.getBlockState(pos).is(ElysianRealm.ELYSIAN_PORTAL_FRAME.get())) {
            Direction face = event.getFace();
            if (face != null) {
                BlockPos targetPos = pos.relative(face);
                if (level.isEmptyBlock(targetPos)) {
                    Optional<ElysianPortalShape> shapeOpt = ElysianPortalShape.findShape(level, targetPos);
                    if (shapeOpt.isPresent()) {
                        ElysianPortalShape shape = shapeOpt.get();

                        BlockPos bottomLeft = shape.getBottomLeft();
                        Direction.Axis axis = shape.getAxis();

                        for (int w = 0; w < shape.getWidth(); w++) {
                            for (int h = 0; h < shape.getHeight(); h++) {
                                BlockPos p = bottomLeft.relative(shape.getRightDir(), w).above(h);
                                level.setBlockAndUpdate(p, ElysianRealm.ELYSIAN_PORTAL.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, axis));
                            }
                        }

                        level.playSound(null, targetPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.playSound(null, targetPos, SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 0.5F, 1.2F);

                        if (!player.isCreative()) {
                            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                        }

                        event.setCanceled(true);
                        event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
                    }
                }
            }
        }
    }
}
