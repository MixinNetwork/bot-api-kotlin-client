package one.mixin.bot.safe

class SafeException : Exception {
    constructor(message: String) : super(message)

    constructor() : super()

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
