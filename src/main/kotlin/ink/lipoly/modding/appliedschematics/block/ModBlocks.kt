@file:Suppress("HasPlatformType")

package ink.lipoly.modding.appliedschematics.block

import ink.lipoly.modding.appliedschematics.AppliedSchematics
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredRegister

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModBlocks {
    val REGISTRY: DeferredRegister.Blocks = DeferredRegister.createBlocks(AppliedSchematics.ID)

    val SCHEMATICANNON_PATHWAY = REGISTRY.register("schematicannon_pathway") { ->
        SchematicannonPathwayBlock(BlockBehaviour.Properties.of().strength(2.0f).noOcclusion())
    }

    val EXPLOSIVE = REGISTRY.register("explosive") { ->
        ExplosiveBlock(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion())
    }
}
