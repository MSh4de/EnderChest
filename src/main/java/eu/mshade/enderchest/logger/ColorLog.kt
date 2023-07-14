package eu.mshade.enderchest.logger

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.color.ANSIConstants
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase

class ColorLog: ForegroundCompositeConverterBase<ILoggingEvent>() {

    override fun getForegroundColorCode(event: ILoggingEvent): String {
        val level = event.level

        return when(level.levelInt){
            Level.ERROR_INT -> ANSIConstants.BOLD + ANSIConstants.RED_FG
            Level.WARN_INT -> ANSIConstants.YELLOW_FG
            Level.INFO_INT -> ANSIConstants.CYAN_FG
            Level.DEBUG_INT -> ANSIConstants.MAGENTA_FG
            else -> ANSIConstants.DEFAULT_FG
        }
    }
}