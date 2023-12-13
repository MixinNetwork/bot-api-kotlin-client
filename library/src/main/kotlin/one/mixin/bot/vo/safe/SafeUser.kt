package one.mixin.bot.vo.safe

data class SafeUser(
    val userId: String,
    val sessionId: String,
    val sessionPrivateKey: ByteArray,
    val serverPublicKey: ByteArray? = null,
    var spendPrivateKey: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SafeUser

        if (userId != other.userId) return false
        if (sessionId != other.sessionId) return false
        if (!sessionPrivateKey.contentEquals(other.sessionPrivateKey)) return false
        if (!serverPublicKey.contentEquals(other.serverPublicKey)) return false
        if (spendPrivateKey != null) {
            if (other.spendPrivateKey == null) return false
            if (!spendPrivateKey.contentEquals(other.spendPrivateKey)) return false
        } else if (other.spendPrivateKey != null) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + sessionId.hashCode()
        result = 31 * result + sessionPrivateKey.contentHashCode()
        result = 31 * result + serverPublicKey.contentHashCode()
        result = 31 * result + (spendPrivateKey?.contentHashCode() ?: 0)
        return result
    }
}
