package com.elysianrealm;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerPlayer;

public class ElysianElfEntity extends PathfinderMob implements Merchant {

    private Player tradingPlayer;
    private MerchantOffers offers;

    public ElysianElfEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        Level level = this.level();

        if (!level.isClientSide) {
            this.setTradingPlayer(player);
            // Open standard trading GUI screen and send offers to client
            java.util.OptionalInt containerId = player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new MerchantMenu(id, inv, this), 
                this.getDisplayName()
            ));
            if (containerId.isPresent()) {
                MerchantOffers merchantOffers = this.getOffers();
                if (!merchantOffers.isEmpty()) {
                    player.sendMerchantOffers(
                        containerId.getAsInt(), 
                        merchantOffers, 
                        1, // Merchant Level
                        this.getVillagerXp(), 
                        this.showProgressBar(), 
                        false // canRestock
                    );
                }
            }
            this.playSound(SoundEvents.ALLAY_AMBIENT_WITH_ITEM, 1.0F, this.getVoicePitch());
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // --- Merchant Interface Implementation ---

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        this.tradingPlayer = player;
    }

    @Override
    @Nullable
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            
            // Trade 1: 3 Emeralds -> 4 Glow Berries
            this.offers.add(new MerchantOffer(
                new ItemStack(Items.EMERALD, 3),
                new ItemStack(Items.GLOW_BERRIES, 4),
                12, 2, 0.05F
            ));
            
            // Trade 2: 8 Emeralds -> 1 Amethyst Shard
            this.offers.add(new MerchantOffer(
                new ItemStack(Items.EMERALD, 8),
                new ItemStack(Items.AMETHYST_SHARD, 1),
                12, 2, 0.05F
            ));
            
            // Trade 3: 16 Emeralds -> 1 Golden Apple
            this.offers.add(new MerchantOffer(
                new ItemStack(Items.EMERALD, 16),
                new ItemStack(Items.GOLDEN_APPLE, 1),
                12, 5, 0.05F
            ));
            
            // Trade 4: 24 Emeralds -> 1 Enchanted Book (Sharpness II)
            ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
            enchantedBook.getOrCreateTag();
            net.minecraft.world.item.EnchantedBookItem.addEnchantment(
                enchantedBook,
                new net.minecraft.world.item.enchantment.EnchantmentInstance(
                    net.minecraft.world.item.enchantment.Enchantments.SHARPNESS, 2
                )
            );
            this.offers.add(new MerchantOffer(
                new ItemStack(Items.EMERALD, 24),
                enchantedBook,
                5, 10, 0.05F
            ));
            
            // Trade 5: 5 Emeralds + 1 Gold Ingot -> 2 Golden Carrots
            this.offers.add(new MerchantOffer(
                new ItemStack(Items.EMERALD, 5),
                new ItemStack(Items.GOLD_INGOT, 1),
                new ItemStack(Items.GOLDEN_CARROT, 2),
                12, 5, 0.05F
            ));
        }
        return this.offers;
    }

    @Override
    public void overrideOffers(MerchantOffers offers) {
        this.offers = offers;
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        // Spawn happy trade particles
        this.level().broadcastEntityEvent(this, (byte) 18);
        this.playSound(this.getNotifyTradeSound(), 1.0F, this.getVoicePitch());
        if (!this.level().isClientSide() && this.getTradingPlayer() instanceof ServerPlayer sPlayer) {
            ElysianFactionSavedData.get(sPlayer.serverLevel()).addReputation(sPlayer.getUUID(), "elves", 2);
            sPlayer.sendSystemMessage(Component.literal("§aGained 2 reputation with elves for trading."));
        }
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int xp) {
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.ALLAY_AMBIENT_WITH_ITEM;
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide();
    }

    // --- Sound Overrides ---

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }
}
