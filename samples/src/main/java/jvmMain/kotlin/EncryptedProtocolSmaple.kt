import one.mixin.bot.extension.toByteArray
import one.mixin.bot.util.EncryptedProtocol
import one.mixin.bot.util.generateEd25519KeyPair
import one.mixin.bot.util.publicKeyToCurve25519
import java.util.*

@ExperimentalUnsignedTypes
fun main() {
    // init sender key pair
    val senderKeyPair = generateEd25519KeyPair()
    // init receiver key pair
    val receiverKeyPair = generateEd25519KeyPair()
    val receiverCurvePublicKey = publicKeyToCurve25519(receiverKeyPair.publicKey)
    val receiverSessionId = UUID.randomUUID().toString()

    // origin message to send
    val message = "Hello Mixin".toByteArray()

    val encryptedProtocol = EncryptedProtocol()

    // encrypt message with receiver's public key and session id
    val encryptedMessage =
        encryptedProtocol.encryptMessage(senderKeyPair, message, receiverCurvePublicKey, receiverSessionId)

    // send to receiver
    // ...

    // receive message and decrypt with self private key
    val decryptedMessage =
        encryptedProtocol.decryptMessage(
            receiverKeyPair,
            UUID.fromString(receiverSessionId).toByteArray(),
            encryptedMessage,
        )

    println("decrypted message equals origin message is ${decryptedMessage.contentEquals(message)}")
}
