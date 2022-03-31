import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.EdDSAPublicKey
import one.mixin.bot.extension.toByteArray
import one.mixin.bot.util.EncryptedProtocol
import one.mixin.bot.util.generateEd25519KeyPair
import one.mixin.bot.util.publicKeyToCurve25519
import java.util.*

@ExperimentalUnsignedTypes
fun main() {
    // init sender key pair
    val senderKeyPair = generateEd25519KeyPair()
    val senderPrivateKey = senderKeyPair.private as EdDSAPrivateKey

    // init receiver key pair
    val receiverKeyPair = generateEd25519KeyPair()
    val receiverPrivateKey = receiverKeyPair.private as EdDSAPrivateKey
    val receiverPublicKey = receiverKeyPair.public as EdDSAPublicKey
    val receiverCurvePublicKey = publicKeyToCurve25519(receiverPublicKey)
    val receiverSessionId = UUID.randomUUID().toString()

    // origin message to send
    val message = "Hello Mixin".toByteArray()

    val encryptedProtocol = EncryptedProtocol()

    // encrypt message with receiver's public key and session id
    val encryptedMessage =
        encryptedProtocol.encryptMessage(senderPrivateKey, message, receiverCurvePublicKey, receiverSessionId)

    // send to receiver
    // ...

    // receive message and decrypt with self private key
    val decryptedMessage = encryptedProtocol.decryptMessage(
        receiverPrivateKey,
        UUID.fromString(receiverSessionId).toByteArray(),
        encryptedMessage
    )

    println("decrypted message equals origin message is ${decryptedMessage.contentEquals(message)}")
}
