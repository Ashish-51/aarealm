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

public class ElysianBeastfolkEntity extends PathfinderMob implements Merchant {

    private Player tradingPlayer;
    private MerchantOffers offers;

    public ElysianBeastfolkEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        Level level = this.level();

        if (!level.isClientSide) {
            this.setTradingPlayer(player);
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
                        1, 
                        this.getVillagerXp(), 
                        this.showProgressBar(), 
                        false
                    );
                }
            }
            this.playSound(SoundEvents.VILLAGER_TRADE, 1.0F, this.getVoicePitch());
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
            
            // Trade 1: 4 Leather -> 1 Emerald
            this.offers.add(new MerchantOffer(
                new ItemStack(Items.LEATHER, 4),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
            ));
            
            // Trade 2: 12 Bones -> 1 Emerald
            this.offers.add(new MerchantOffer(
                new ItemStack(Items.BONE, 12),
                new ItemStack(Items.EMERALD, 1),
                16, 2, 0.05F
            ));
            
            // Trade 3: 3 Emeralds -> 8 Cooked Mutton
            this.offers.add(new MerchantOffer(
                new ItemStack(Items.EMERALD, 3),
                new ItemStack(Items.COOKED_MUTTON, 8),
                12, 3, 0.05F
            ));
            
            // Trade 4: 8 Emeralds -> 1 Bow
            this.offers.add(new MerchantOffer(
                new ItemStack(Items.EMERALD, 8),
                new ItemStack(Items.BOW, 1),
                12, 5, 0.05F
            ));
            
            // Trade 5: 4 Emeralds + 1 Rabbit Foot -> 1 Potion of Swiftness (Speed II, 1:30)
            ItemStack swiftnessPotion = new ItemStack(Items.POTION);
            net.minecraft.world.item.alchemy.PotionUtils.setPotion(swiftnessPotion, net.minecraft.world.item.alchemy.Potions.STRONG_SWIFTNESS);
            this.offers.add(new MerchantOffer(
                new ItemStack(Items.EMERALD, 4),
                new ItemStack(Items.RABBIT_FOOT, 1),
                swiftnessPotion,
                5, 10, 0.05F
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
        this.level().broadcastEntityEvent(this, (byte) 18);
        this.playSound(this.getNotifyTradeSound(), 1.0F, this.getVoicePitch());
        if (!this.level().isClientSide() && this.getTradingPlayer() instanceof ServerPlayer sPlayer) {
            ElysianFactionSavedData.get(sPlayer.serverLevel()).addReputation(sPlayer.getUUID(), "beastfolk", 2);
            sPlayer.sendSystemMessage(Component.literal("§aGained 2 reputation with beastfolk for trading."));
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
        return SoundEvents.VILLAGER_YES;
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide();
    }

    // --- Sound Overrides ---

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FOX_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.FOX_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FOX_DEATH;
    }
}
