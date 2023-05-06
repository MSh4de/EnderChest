package eu.mshade.enderchest.listener.packet

import eu.mshade.enderframe.entity.metadata.FlyingEntityMetadata
import eu.mshade.enderframe.packetevent.MinecraftPacketToggleFlyingEvent
import eu.mshade.mwork.event.EventListener

class MinecraftPacketToggleFlyingListener : EventListener<MinecraftPacketToggleFlyingEvent> {

    override fun onEvent(event: MinecraftPacketToggleFlyingEvent) {
        val player = event.player
        val metadataKeyValueBucket = player.metadataKeyValueBucket
        metadataKeyValueBucket.setMetadataKeyValue(FlyingEntityMetadata(event.isFlying))
    }

}