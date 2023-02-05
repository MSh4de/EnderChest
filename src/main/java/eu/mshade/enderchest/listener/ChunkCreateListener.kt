package eu.mshade.enderchest.listener

import eu.mshade.axolotl.protocol.AxolotlProtocolRepository
import eu.mshade.enderchest.axolotl.AxololtConnection
import eu.mshade.enderframe.event.ChunkCreateEvent
import eu.mshade.mwork.event.EventListener

class ChunkCreateListener : EventListener<ChunkCreateEvent> {

    override fun onEvent(event: ChunkCreateEvent) {
        AxololtConnection.send {
            it.sendChunk(event.chunk)
        }
    }

}