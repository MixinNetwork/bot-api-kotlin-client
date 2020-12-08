package one.mixin.bot

// Each time you use the password, you need to save the number of uses and bring in the encryption.
interface PinIterator {
    fun getValue(): Long
    fun increment()
}
