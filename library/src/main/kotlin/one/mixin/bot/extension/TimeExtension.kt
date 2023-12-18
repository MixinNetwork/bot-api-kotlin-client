package one.mixin.bot.extension

import java.time.Instant

fun nowInUtcNano(): Long {
    val inst = Instant.now()
    var time = inst.epochSecond
    time *= 1000000000L
    time += inst.nano.toLong()
    return time
}
