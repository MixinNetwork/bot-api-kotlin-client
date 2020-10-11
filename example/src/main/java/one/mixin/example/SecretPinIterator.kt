package one.mixin.example

import one.mixin.bot.PinIterator

class SecretPinIterator : PinIterator {
    private var currentCount = 0L
    override fun getValue(): Long {
        return currentCount
    }

    override fun increment() {
        currentCount++
    }
}