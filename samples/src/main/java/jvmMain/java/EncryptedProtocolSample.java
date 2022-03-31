package jvmMain.java;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import one.mixin.bot.extension.UUIDExtensionKt;
import one.mixin.bot.util.EncryptedProtocol;

import java.security.KeyPair;
import java.util.Arrays;
import java.util.UUID;

import static one.mixin.bot.util.CryptoUtilKt.generateEd25519KeyPair;
import static one.mixin.bot.util.CryptoUtilKt.publicKeyToCurve25519;

class EncryptedProtocolSample {
    public static void main(String[] args) {
        // init sender key pair
        KeyPair senderKeyPair = generateEd25519KeyPair();
        net.i2p.crypto.eddsa.EdDSAPrivateKey senderPrivateKey = (EdDSAPrivateKey) senderKeyPair.getPrivate();

        // init receiver key pair
        KeyPair receiverKeyPair = generateEd25519KeyPair();
        EdDSAPrivateKey receiverPrivateKey = (EdDSAPrivateKey) receiverKeyPair.getPrivate();
        EdDSAPublicKey receiverPublicKey = (EdDSAPublicKey) receiverKeyPair.getPublic();
        byte[] receiverCurvePublicKey = publicKeyToCurve25519(receiverPublicKey);
        String receiverSessionId = UUID.randomUUID().toString();

        // origin message to send
        byte[] message = "Hello Mixin".getBytes();

        EncryptedProtocol encryptedProtocol = new EncryptedProtocol();

        // encrypt message with receiver's public key and session id
        byte[] encryptedMessage =
                encryptedProtocol.encryptMessage(senderPrivateKey, message, receiverCurvePublicKey, receiverSessionId, null, null);

        // send to receiver
        // ...

        // receive message and decrypt with self private key
        byte[] decryptedMessage = encryptedProtocol.decryptMessage(
                receiverPrivateKey,
                UUIDExtensionKt.toByteArray(UUID.fromString(receiverSessionId)),
                encryptedMessage
        );

        System.out.println("decrypted message equals origin message is " + Arrays.equals(decryptedMessage, message));
    }
}