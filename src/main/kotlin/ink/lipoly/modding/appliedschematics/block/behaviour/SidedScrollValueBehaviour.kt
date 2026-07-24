package ink.lipoly.modding.appliedschematics.block.behaviour

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter
import com.simibubi.create.foundation.utility.CreateLang
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

class SidedScrollValueBehaviour(
    private var label: Component,
    be: SmartBlockEntity,
) : BlockEntityBehaviour(be), ValueSettingsBehaviour {

    companion object {
        val TYPE = BehaviourType<SidedScrollValueBehaviour>()
        const val MAX_RADIUS = 16
    }

    val values = IntArray(6) { 3 }
    private var min = 0
    private var max = MAX_RADIUS
    private var currentFace: Direction = Direction.NORTH
    var callback: ((Direction, Int) -> Unit)? = null

    override fun getType(): BehaviourType<*> = TYPE

    override fun testHit(hit: Vec3): Boolean {
        val state = blockEntity.blockState
        val localHit = hit.subtract(Vec3.atLowerCornerOf(blockEntity.blockPos))
        for (face in Direction.entries) {
            val transform = CenteredSideValueBoxTransform().fromSide(face) as ValueBoxTransform
            if (transform.testHit(world, pos, state, localHit)) {
                currentFace = face.opposite
                return true
            }
        }
        return false
    }

    override fun isActive(): Boolean = true

    override fun getSlotPositioning(): ValueBoxTransform {
        return CenteredSideValueBoxTransform().fromSide(currentFace) as ValueBoxTransform
    }

    override fun createBoard(player: Player, hitResult: BlockHitResult): ValueSettingsBoard {
        val faceName = Component.translatable("direction.appliedschematics.${currentFace.serializedName}")
        return ValueSettingsBoard(
            label.copy().append(" (").append(faceName).append(")"),
            max,
            10,
            listOf(Component.literal("Value")),
            ValueSettingsFormatter { s: ValueSettingsBehaviour.ValueSettings -> CreateLang.number(s.value().toDouble()).component() },
        )
    }

    override fun setValueSettings(
        player: Player,
        valueSetting: ValueSettingsBehaviour.ValueSettings,
        ctrlDown: Boolean,
    ) {
        if (valueSetting == getValueSettings()) return
        val clamped = valueSetting.value().coerceIn(min, max)
        setValue(currentFace, clamped)
        playFeedbackSound(this)
    }

    override fun getValueSettings(): ValueSettingsBehaviour.ValueSettings {
        return ValueSettingsBehaviour.ValueSettings(0, getValue(currentFace))
    }

    override fun onlyVisibleWithWrench(): Boolean = true

    override fun write(nbt: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        nbt.putIntArray("SidedScrollValues", values.toList())
        super.write(nbt, registries, clientPacket)
    }

    override fun read(nbt: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        val saved = nbt.getIntArray("SidedScrollValues")
        if (saved.size == 6) {
            saved.copyInto(values)
        }
        super.read(nbt, registries, clientPacket)
    }

    fun between(min: Int, max: Int): SidedScrollValueBehaviour {
        this.min = min
        this.max = max
        return this
    }

    fun withCallback(callback: (Direction, Int) -> Unit): SidedScrollValueBehaviour {
        this.callback = callback
        return this
    }

    fun setValue(face: Direction, value: Int) {
        values[face.ordinal] = value.coerceIn(min, max)
        callback?.invoke(face, values[face.ordinal])
        blockEntity.setChanged()
        blockEntity.sendData()
    }

    fun getValue(face: Direction): Int = values[face.ordinal]
}
