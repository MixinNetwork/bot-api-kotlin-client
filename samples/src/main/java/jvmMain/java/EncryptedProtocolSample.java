package jvmMain.java;

import one.mixin.bot.extension.UUIDExtensionKt;
import one.mixin.bot.tip.EdKeyPair;
import one.mixin.bot.util.EncryptedProtocol;

import java.util.Arrays;
import java.util.UUID;

import static one.mixin.bot.util.CryptoUtilKt.generateEd25519KeyPair;
import static one.mixin.bot.util.CryptoUtilKt.publicKeyToCurve25519;

class EncryptedProtocolSample {
    public static void main(String[] args) {
        // init sender key pair
        EdKeyPair senderKeyPair = generateEd25519KeyPair();

        // init receiver key pair
        EdKeyPair receiverKeyPair = generateEd25519KeyPair();
        byte[] receiverCurvePublicKey = publicKeyToCurve25519(receiverKeyPair.getPublicKey());
        String receiverSessionId = UUID.randomUUID().toString();

        // origin message to send
        byte[] message = "Hello Mixin".getBytes();

        EncryptedProtocol encryptedProtocol = new EncryptedProtocol();

        // encrypt message with receiver's public key and session id
        byte[] encryptedMessage =
                encryptedProtocol.encryptMessage(senderKeyPair, message, receiverCurvePublicKey, receiverSessionId, null, null);

        // send to receiver
        // ...

        // receive message and decrypt with self private key
        byte[] decryptedMessage = encryptedProtocol.decryptMessage(
                receiverKeyPair,
                UUIDExtensionKt.toByteArray(UUID.fromString(receiverSessionId)),
                encryptedMessage
        );

        System.out.println("decrypted message equals origin message is " + Arrays.equals(decryptedMessage, message));
    }
}