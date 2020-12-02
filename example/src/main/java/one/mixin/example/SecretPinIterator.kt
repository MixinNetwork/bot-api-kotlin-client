package one.mixin.example

import one.mixin.bot.PinIterator

/**
 * This is just an example of how you need to keep it, and for each bot (or user) the number of PINs used will only increase,
 * not decrease. Be sure to keep this data safe.
 */
class SecretPinIterator : PinIterator {
    override fun getValue(): Long {
        return System.currentTimeMillis()
    }

    override fun increment() {
    }
}