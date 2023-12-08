package one.mixin.bot.tip

class TipException : Exception {
    constructor(message: String) : super(message)

    constructor() : super()

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}