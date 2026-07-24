package ink.lipoly.modding.appliedschematics

import ink.lipoly.modding.appliedschematics.block.ModBlocks
import ink.lipoly.modding.appliedschematics.blockentity.ModBlockEntities
import ink.lipoly.modding.appliedschematics.blockentity.SchematicannonPathwayBlockEntity
import ink.lipoly.modding.appliedschematics.item.ModItems
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(AppliedSchematics.ID)
object AppliedSchematics {
    const val ID = "appliedschematics"

    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        ModBlocks.REGISTRY.register(MOD_BUS)
        ModItems.REGISTRY.register(MOD_BUS)
        ModBlockEntities.REGISTRY.register(MOD_BUS)
        MOD_BUS.addListener(SchematicannonPathwayBlockEntity::registerCapabilities)
        MOD_BUS.addListener(this::addCreative)
    }

    private fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        val ae2TabKey: ResourceKey<CreativeModeTab> = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath("ae2", "main")
        )
        if (event.tabKey == ae2TabKey) {
            event.accept(ModItems.SCHEMATICANNON_PATHWAY.get())
            event.accept(ModItems.EXPLOSIVE.get())
        }
    }
}
