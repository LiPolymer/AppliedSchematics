package ink.lipoly.modding.appliedschematic

import ink.lipoly.modding.appliedschematic.block.ModBlocks
import ink.lipoly.modding.appliedschematic.blockentity.ModBlockEntities
import ink.lipoly.modding.appliedschematic.blockentity.SchematicannonPathwayBlockEntity
import ink.lipoly.modding.appliedschematic.item.ModItems
import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(AppliedSchematic.ID)
object AppliedSchematic {
    const val ID = "appliedschematic"

    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        ModBlocks.REGISTRY.register(MOD_BUS)
        ModItems.REGISTRY.register(MOD_BUS)
        ModBlockEntities.REGISTRY.register(MOD_BUS)
        MOD_BUS.addListener(SchematicannonPathwayBlockEntity::registerCapabilities)
    }
}
