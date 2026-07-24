package ink.lipoly.modding.appliedschematics.blockentity

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import ink.lipoly.modding.appliedschematics.block.behaviour.SidedScrollValueBehaviour
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.block.state.BlockState

class ExplosiveBlockEntity(pos: BlockPos, state: BlockState) :
    SmartBlockEntity(ModBlockEntities.EXPLOSIVE.get(), pos, state) {

    var upperCorner: BlockPos = pos
        private set
    var lowerCorner: BlockPos = pos
        private set

    private var initialized = false

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour>) {
        val scrollBehaviour = SidedScrollValueBehaviour(
            Component.translatable("appliedschematics.explosive.radius"),
            this,
        )
            .between(0, SidedScrollValueBehaviour.MAX_RADIUS)
            .withCallback { _, _ -> updateExcavationArea() }

        behaviours.add(scrollBehaviour)
    }

    override fun tick() {
        super.tick()
        if (!initialized && level != null) {
            initialized = true
            updateExcavationArea()
        }
    }

    fun updateExcavationArea() {
        val scrollBehaviour = getBehaviour(SidedScrollValueBehaviour.TYPE) ?: return
        var upper = blockPos
        var lower = blockPos

        for (dir in Direction.entries) {
            val r = scrollBehaviour.getValue(dir)
            when (dir.axisDirection) {
                Direction.AxisDirection.POSITIVE -> upper = upper.relative(dir, r + 1)
                Direction.AxisDirection.NEGATIVE -> lower = lower.relative(dir, r)
            }
        }

        upperCorner = upper
        lowerCorner = lower
        sendData()
    }

    fun explode(source: LivingEntity?) {
        val currentLevel = level ?: return
        if (currentLevel.isClientSide) return
        val serverLevel = currentLevel as? ServerLevel ?: return

        val damageSource = currentLevel.damageSources().explosion(source, null)

        val aabb = net.minecraft.world.phys.AABB(
            lowerCorner.x.toDouble(), lowerCorner.y.toDouble(), lowerCorner.z.toDouble(),
            upperCorner.x.toDouble() + 1, upperCorner.y.toDouble() + 1, upperCorner.z.toDouble() + 1,
        )
        currentLevel.getEntities(null, aabb).forEach { entity ->
            if (entity is LivingEntity && entity !== source) {
                val dist = entity.position().distanceTo(blockPos.center)
                val maxDist = SidedScrollValueBehaviour.MAX_RADIUS.toDouble()
                if (dist < maxDist) {
                    val damage = ((1.0 - dist / maxDist) * 20.0).coerceAtLeast(0.0)
                    if (damage > 0.0) {
                        entity.hurt(damageSource, damage.toFloat())
                    }
                }
            }
        }

        net.minecraft.core.BlockPos.betweenClosedStream(lowerCorner, upperCorner).forEach { targetPos ->
            if (targetPos != blockPos) {
                currentLevel.removeBlock(targetPos.immutable(), false)
            }
        }

        currentLevel.playSound(
            null, blockPos,
            SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
            4.0f, 0.7f + currentLevel.random.nextFloat() * 0.6f,
        )

        for (x in lowerCorner.x..upperCorner.x step 2) {
            for (y in lowerCorner.y..upperCorner.y step 2) {
                for (z in lowerCorner.z..upperCorner.z step 2) {
                    serverLevel.sendParticles(
                        ParticleTypes.EXPLOSION,
                        x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5,
                        1, 0.0, 0.0, 0.0, 0.15,
                    )
                }
            }
        }

        currentLevel.removeBlock(blockPos, false)
    }

    override fun write(tag: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        super.write(tag, registries, clientPacket)
        tag.putInt("UpperX", upperCorner.x)
        tag.putInt("UpperY", upperCorner.y)
        tag.putInt("UpperZ", upperCorner.z)
        tag.putInt("LowerX", lowerCorner.x)
        tag.putInt("LowerY", lowerCorner.y)
        tag.putInt("LowerZ", lowerCorner.z)
    }

    override fun read(tag: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        super.read(tag, registries, clientPacket)
        upperCorner = BlockPos(
            tag.getInt("UpperX"),
            tag.getInt("UpperY"),
            tag.getInt("UpperZ"),
        )
        lowerCorner = BlockPos(
            tag.getInt("LowerX"),
            tag.getInt("LowerY"),
            tag.getInt("LowerZ"),
        )
    }
}
