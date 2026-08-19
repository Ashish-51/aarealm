package com.elysianrealm;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

@Mod(ElysianRealm.MODID)
public class ElysianRealm {
    public static final String MODID = "elysianrealm";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Deferred Registers
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, MODID);
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, MODID);

    // Blocks
    public static final RegistryObject<Block> ELYSIAN_PORTAL_FRAME = BLOCKS.register("elysian_portal_frame",
            () -> new ElysianPortalFrameBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(10.0F, 1200.0F)
                    .sound(SoundType.STONE)
            )
    );

    public static final RegistryObject<Block> ELYSIAN_PORTAL = BLOCKS.register("elysian_portal",
            () -> new ElysianPortalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .noCollission()
                    .noLootTable()
                    .strength(0.5F)
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> 11)
                    .noOcclusion()
            )
    );

    // Block Items
    public static final RegistryObject<Item> ELYSIAN_PORTAL_FRAME_ITEM = ITEMS.register("elysian_portal_frame",
            () -> new BlockItem(ELYSIAN_PORTAL_FRAME.get(), new Item.Properties())
    );

    // Entities
    public static final RegistryObject<EntityType<ElysianElfEntity>> ELYSIAN_ELF = ENTITIES.register("elysian_elf",
            () -> EntityType.Builder.of(ElysianElfEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(MODID, "elysian_elf").toString())
    );

    public static final RegistryObject<EntityType<ElysianBeastfolkEntity>> ELYSIAN_BEASTFOLK = ENTITIES.register("elysian_beastfolk",
            () -> EntityType.Builder.of(ElysianBeastfolkEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(MODID, "elysian_beastfolk").toString())
    );

    public static final RegistryObject<EntityType<ElysianHumanEntity>> ELYSIAN_HUMAN = ENTITIES.register("elysian_human",
            () -> EntityType.Builder.of(ElysianHumanEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(MODID, "elysian_human").toString())
    );

    public static final RegistryObject<EntityType<ElysianDwarfEntity>> ELYSIAN_DWARF = ENTITIES.register("elysian_dwarf",
            () -> EntityType.Builder.of(ElysianDwarfEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(MODID, "elysian_dwarf").toString())
    );

    public static final RegistryObject<EntityType<ElysianGoblinEntity>> ELYSIAN_GOBLIN = ENTITIES.register("elysian_goblin",
            () -> EntityType.Builder.of(ElysianGoblinEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(MODID, "elysian_goblin").toString())
    );

    public static final RegistryObject<EntityType<ElysianOrcEntity>> ELYSIAN_ORC = ENTITIES.register("elysian_orc",
            () -> EntityType.Builder.of(ElysianOrcEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(MODID, "elysian_orc").toString())
    );

    // Spawn Eggs
    public static final RegistryObject<Item> ELYSIAN_ELF_SPAWN_EGG = ITEMS.register("elysian_elf_spawn_egg",
            () -> new ForgeSpawnEggItem(ELYSIAN_ELF, 0x1f5f3f, 0x7ddad3, new Item.Properties())
    );

    public static final RegistryObject<Item> ELYSIAN_BEASTFOLK_SPAWN_EGG = ITEMS.register("elysian_beastfolk_spawn_egg",
            () -> new ForgeSpawnEggItem(ELYSIAN_BEASTFOLK, 0x7c4c24, 0xfce4c4, new Item.Properties())
    );

    public static final RegistryObject<Item> ELYSIAN_HUMAN_SPAWN_EGG = ITEMS.register("elysian_human_spawn_egg",
            () -> new ForgeSpawnEggItem(ELYSIAN_HUMAN, 0xc2a67f, 0x4f3d2f, new Item.Properties())
    );

    public static final RegistryObject<Item> ELYSIAN_DWARF_SPAWN_EGG = ITEMS.register("elysian_dwarf_spawn_egg",
            () -> new ForgeSpawnEggItem(ELYSIAN_DWARF, 0x5a5a5a, 0xe3e3e3, new Item.Properties())
    );

    public static final RegistryObject<Item> ELYSIAN_GOBLIN_SPAWN_EGG = ITEMS.register("elysian_goblin_spawn_egg",
            () -> new ForgeSpawnEggItem(ELYSIAN_GOBLIN, 0x2e6b22, 0xe84a23, new Item.Properties())
    );

    public static final RegistryObject<Item> ELYSIAN_ORC_SPAWN_EGG = ITEMS.register("elysian_orc_spawn_egg",
            () -> new ForgeSpawnEggItem(ELYSIAN_ORC, 0x3d4732, 0xa31c1c, new Item.Properties())
    );

    // Structures
    public static final RegistryObject<StructureType<ElvenSettlementStructure>> ELVEN_SETTLEMENT_STRUCTURE = STRUCTURE_TYPES.register("elven_settlement",
            () -> () -> ElvenSettlementStructure.CODEC
    );
    public static final RegistryObject<StructurePieceType> ELVEN_SETTLEMENT_PIECE = STRUCTURE_PIECE_TYPES.register("elven_settlement_piece",
            () -> ElvenSettlementPiece::new
    );

    public static final RegistryObject<StructureType<BeastfolkSettlementStructure>> BEASTFOLK_SETTLEMENT_STRUCTURE = STRUCTURE_TYPES.register("beastfolk_settlement",
            () -> () -> BeastfolkSettlementStructure.CODEC
    );
    public static final RegistryObject<StructurePieceType> BEASTFOLK_SETTLEMENT_PIECE = STRUCTURE_PIECE_TYPES.register("beastfolk_settlement_piece",
            () -> BeastfolkSettlementPiece::new
    );

    public static final RegistryObject<StructureType<HumanSettlementStructure>> HUMAN_SETTLEMENT_STRUCTURE = STRUCTURE_TYPES.register("human_settlement",
            () -> () -> HumanSettlementStructure.CODEC
    );
    public static final RegistryObject<StructurePieceType> HUMAN_SETTLEMENT_PIECE = STRUCTURE_PIECE_TYPES.register("human_settlement_piece",
            () -> HumanSettlementPiece::new
    );

    public static final RegistryObject<StructureType<DwarvenSettlementStructure>> DWARVEN_SETTLEMENT_STRUCTURE = STRUCTURE_TYPES.register("dwarven_settlement",
            () -> () -> DwarvenSettlementStructure.CODEC
    );
    public static final RegistryObject<StructurePieceType> DWARVEN_SETTLEMENT_PIECE = STRUCTURE_PIECE_TYPES.register("dwarven_settlement_piece",
            () -> DwarvenSettlementPiece::new
    );

    public static final RegistryObject<StructureType<GoblinSettlementStructure>> GOBLIN_SETTLEMENT_STRUCTURE = STRUCTURE_TYPES.register("goblin_settlement",
            () -> () -> GoblinSettlementStructure.CODEC
    );
    public static final RegistryObject<StructurePieceType> GOBLIN_SETTLEMENT_PIECE = STRUCTURE_PIECE_TYPES.register("goblin_settlement_piece",
            () -> GoblinSettlementPiece::new
    );

    public static final RegistryObject<StructureType<OrcSettlementStructure>> ORC_SETTLEMENT_STRUCTURE = STRUCTURE_TYPES.register("orc_settlement",
            () -> () -> OrcSettlementStructure.CODEC
    );
    public static final RegistryObject<StructurePieceType> ORC_SETTLEMENT_PIECE = STRUCTURE_PIECE_TYPES.register("orc_settlement_piece",
            () -> OrcSettlementPiece::new
    );

    // Creative Tab
    public static final RegistryObject<CreativeModeTab> ELYSIAN_TAB = CREATIVE_MODE_TABS.register("elysian_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.elysianrealm"))
            .icon(() -> ELYSIAN_PORTAL_FRAME_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ELYSIAN_PORTAL_FRAME_ITEM.get());
                output.accept(ELYSIAN_ELF_SPAWN_EGG.get());
                output.accept(ELYSIAN_BEASTFOLK_SPAWN_EGG.get());
                output.accept(ELYSIAN_HUMAN_SPAWN_EGG.get());
                output.accept(ELYSIAN_DWARF_SPAWN_EGG.get());
                output.accept(ELYSIAN_GOBLIN_SPAWN_EGG.get());
                output.accept(ELYSIAN_ORC_SPAWN_EGG.get());
            }).build());

    // Resource Keys for Dimension and Dimension Type
    public static final ResourceKey<Level> ELYSIAN_LEVEL_KEY = ResourceKey.create(
            Registries.DIMENSION,
            new ResourceLocation(MODID, "elysian_realm")
    );
    public static final ResourceKey<DimensionType> ELYSIAN_DIMENSION_TYPE_KEY = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            new ResourceLocation(MODID, "elysian_realm")
    );

    public ElysianRealm(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register Deferred Registers
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        STRUCTURE_TYPES.register(modEventBus);
        STRUCTURE_PIECE_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerEntityAttributes);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ELYSIAN_ELF.get(), ElysianElfEntity.createAttributes().build());
        event.put(ELYSIAN_BEASTFOLK.get(), ElysianBeastfolkEntity.createAttributes().build());
        event.put(ELYSIAN_HUMAN.get(), ElysianHumanEntity.createAttributes().build());
        event.put(ELYSIAN_DWARF.get(), ElysianDwarfEntity.createAttributes().build());
        event.put(ELYSIAN_GOBLIN.get(), ElysianGoblinEntity.createAttributes().build());
        event.put(ELYSIAN_ORC.get(), ElysianOrcEntity.createAttributes().build());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ElysianTeleportCommand.register(event.getDispatcher());
        ElysianReputationCommand.register(event.getDispatcher());
    }
}
