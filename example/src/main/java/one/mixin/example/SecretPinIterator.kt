package one.mixin.example

import one.mixin.bot.PinIterator

/**
 * This is just an example of how you need to keep it, and for each bot (or user) the number of PINs used will only increase,
 * not decrease. Be sure to keep this data safe.
 */
class SecretPinIterator : PinIterator {
    private var currentCount = 0L
    override fun getValue(): Long {
        return currentCount
    }

    override fun increment() {
        currentCount++
        // Please save it and assign it a value when you initialize it.
    }
}