package jvmMain.java;

import one.mixin.bot.HttpClient;
import one.mixin.bot.api.MixinResponse;
import one.mixin.bot.extension.Base64ExtensionKt;
import one.mixin.bot.extension.ByteArrayExtensionKt;
import one.mixin.bot.extension.TimeExtensionKt;
import one.mixin.bot.safe.EdKeyPair;
import one.mixin.bot.safe.TipBody;
import one.mixin.bot.safe.TipException;
import one.mixin.bot.safe.TipKt;
import one.mixin.bot.util.ByteArrayUtilKt;
import one.mixin.bot.util.CryptoUtilKt;
import one.mixin.bot.vo.Account;
import one.mixin.bot.vo.PinRequest;
import one.mixin.bot.vo.User;

import java.io.IOException;
import java.util.Objects;

import static jvmMain.java.Sample.createUser;
import static jvmMain.java.Sample.userPin;
import static one.mixin.bot.SessionKt.encryptPin;
import static one.mixin.bot.SessionKt.encryptTipPin;

public class SafeSample {
    public static void main(String[] args) throws Exception {
        HttpClient botClient = Common.botClient;

        updateFromLegacyPin(botClient);

//         Account user = createTipPin(botClient);
    }

    private static void updateFromLegacyPin(HttpClient botClient) throws Exception {
        // create user
        EdKeyPair sessionKey = CryptoUtilKt.generateEd25519KeyPair();
        String sessionSecret = Base64ExtensionKt.base64Encode(sessionKey.getPublicKey());
        User user = createUser(botClient, sessionSecret);
        assert user != null;

        HttpClient userClient = new HttpClient.Builder().useCNServer().enableDebug().configSafeUser(
                user.getUserId(),
                user.getSessionId(),
                sessionKey.getPrivateKey(),
                null,
                null
        ).build();

        // decrypt pin token
        byte[] userPrivateKey = sessionKey.getPrivateKey();
        byte[] userAesKey = CryptoUtilKt.decryptPinToken(Base64ExtensionKt.base64Decode(user.getPinToken()), userPrivateKey);

        // create user pin
        MixinResponse<User> response = userClient.getUserService().createPinCall(
                new PinRequest(encryptPin(userAesKey, userPin), null, null)
        ).execute().body();
        assert response != null;
        if (response.isSuccess()) {
            System.out.println("Create pin success " + response.getData());
        } else {
            throw new Exception("Create pin failure " + response.getError());
        }
        // verify usr pin
        response = userClient.getUserService().pinVerifyCall(
                new PinRequest(encryptPin(userAesKey, userPin), null, null)
        ).execute().body();
        assert response != null;
        if (response.isSuccess()) {
            System.out.println("Verify pin success");
        } else {
            throw new Exception("Verify pin failure " + response.getError());
        }

        // update tip pin
        byte[] tipSeed = CryptoUtilKt.generateRandomBytes(32);
        EdKeyPair keyPair = CryptoUtilKt.newKeyPairFromSeed(tipSeed);
        TipKt.updateTipPin(userClient, ByteArrayExtensionKt.toHex(keyPair.getPublicKey()), Base64ExtensionKt.base64UrlEncode(userPrivateKey), user.getPinToken(), userPin);
        // verify tip pin
        long timestamp = TimeExtensionKt.nowInUtcNano();
        response = userClient.getUserService().pinVerifyCall(
                new PinRequest(encryptTipPin(userAesKey, TipBody.forVerify(timestamp), keyPair.getPrivateKey()), null, timestamp)
        ).execute().body();
        assert response != null;
        if (response.isSuccess()) {
            System.out.println("Verify tip pin success");
        } else {
            throw new Exception("Verify tip pin failure " + response.getError());
        }

        // register safe
        TipKt.registerSafe(userClient, user.getUserId(), ByteArrayExtensionKt.toHex(keyPair.getPrivateKey()), ByteArrayExtensionKt.toHex(keyPair.getPrivateKey()), Base64ExtensionKt.base64UrlEncode(userPrivateKey), user.getPinToken());
    }

    private static Account createTipPin(HttpClient botClient) throws IOException, TipException {
        // create user
        EdKeyPair sessionKey = CryptoUtilKt.generateEd25519KeyPair();
        String sessionSecret = Base64ExtensionKt.base64Encode(sessionKey.getPublicKey());
        User user = createUser(botClient, sessionSecret);
        assert user != null;

        HttpClient userClient = new HttpClient.Builder().useCNServer().enableDebug().configSafeUser(
                user.getUserId(),
                user.getSessionId(),
                sessionKey.getPrivateKey(),
                null,
                null
        ).build();

        // decrypt pin token
        byte[] userPrivateKey = sessionKey.getPrivateKey();
        byte[] userAesKey = CryptoUtilKt.decryptPinToken(Base64ExtensionKt.base64Decode(user.getPinToken()), userPrivateKey);

        // create user tip pin
        byte[] tipSeed = CryptoUtilKt.generateRandomBytes(32);
        EdKeyPair keyPair = CryptoUtilKt.newKeyPairFromSeed(tipSeed);
        MixinResponse<User> response = userClient.getUserService().createPinCall(
                new PinRequest(
                        encryptPin(userAesKey, Bytes.concat(keyPair.getPublicKey(), ByteArrayUtilKt.toBeByteArray(1L))),
                        null, null
                )).execute().body();
        assert response != null;
        if (response.isSuccess()) {
            System.out.println("Create tip pin success " + Objects.requireNonNull(response.getData()));
        } else {
            System.out.println("Create tip pin failure " + response.getError());
        }

        // register safe
        return TipKt.registerSafe(userClient, user.getUserId(), ByteArrayExtensionKt.toHex(keyPair.getPrivateKey()), ByteArrayExtensionKt.toHex(keyPair.getPrivateKey()), Base64ExtensionKt.base64UrlEncode(userPrivateKey), user.getPinToken());
    }
}
