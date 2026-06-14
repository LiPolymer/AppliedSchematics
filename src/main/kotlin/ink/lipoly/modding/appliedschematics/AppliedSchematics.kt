package ink.lipoly.modding.appliedschematics

import ink.lipoly.modding.appliedschematics.block.ModBlocks
import ink.lipoly.modding.appliedschematics.blockentity.ModBlockEntities
import ink.lipoly.modding.appliedschematics.blockentity.SchematicannonPathwayBlockEntity
import ink.lipoly.modding.appliedschematics.item.ModItems
import net.neoforged.fml.common.Mod
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
    }
}
