package jvmMain.java;

import kotlin.ExceptionsKt;
import kotlin.collections.CollectionsKt;
import one.mixin.bot.HttpClient;
import one.mixin.bot.SessionKt;
import one.mixin.bot.api.MixinResponse;
import one.mixin.bot.extension.Base64ExtensionKt;
import one.mixin.bot.extension.ByteArrayExtensionKt;
import one.mixin.bot.extension.StringExtensionKt;
import one.mixin.bot.safe.*;
import one.mixin.bot.util.ByteArrayUtilKt;
import one.mixin.bot.util.CryptoUtilKt;
import one.mixin.bot.vo.Account;
import one.mixin.bot.vo.PinRequest;
import one.mixin.bot.vo.User;
import one.mixin.bot.vo.safe.MixAddress;
import one.mixin.bot.vo.safe.MixAddressKt;
import one.mixin.bot.vo.safe.TransactionRecipient;
import one.mixin.bot.vo.safe.TransactionResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static jvmMain.java.Sample.*;

public class SafeSample {
    public static void main(String[] args) throws SafeException, IOException, TipException {
        HttpClient botClient = new HttpClient.Builder().useCNServer().configSafeUser(
                Config.userId,
                Config.sessionId,
                Base64ExtensionKt.base64UrlDecode(Config.privateKey),
                Base64ExtensionKt.base64UrlDecode(Config.pinTokenPem),
                ByteArrayExtensionKt.hexStringToByteArray(Config.pin)
            ).enableDebug().build();

         updateFromLegacyPin(botClient);

//         Account user = createTipPin(botClient);

        // use Transaction.kt or MixAddress.kt should load libgojni.so first
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current absolute path is: " + s);
        System.load(s + "/library/libs/darwin/amd64/libgojni.so");

//        transactionToOne(botClient);

//        transactionToMultiple(botClient);
    }

    private static void updateFromLegacyPin(HttpClient botClient) throws IOException, TipException {
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

        // create user's pin
        createPin(userClient, userAesKey);

        // update tip pin
        byte[] tipSeed = CryptoUtilKt.generateRandomBytes(32);
        EdKeyPair keyPair = CryptoUtilKt.newKeyPairFromSeed(tipSeed);
        TipKt.updateTipPin(userClient, ByteArrayExtensionKt.toHex(keyPair.getPublicKey()), Base64ExtensionKt.base64UrlEncode(userPrivateKey), user.getPinToken(), userPin);

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
                        SessionKt.encryptPin(userAesKey, Bytes.concat(keyPair.getPublicKey(), ByteArrayUtilKt.toBeByteArray(1L))),
                        null, null, null
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

    private static void transactionToOne(HttpClient botClient) throws SafeException, IOException {
        String asset = StringExtensionKt.assetIdToAsset("965e5c6e-434c-3fa9-b780-c50f43cd955c"); // cnb
        MixAddress mixAddress = MixAddress.Companion.newUuidMixAddress(CollectionsKt.listOf("d3bee23a-81d4-462e-902a-22dae9ef89ff"), 1);
        assert mixAddress != null;
        TransactionRecipient transactionRecipient = new TransactionRecipient(mixAddress, "0.013", null, null);
        String trace = UUID.randomUUID().toString();
        System.out.println("trace: " + trace);
        List<TransactionResponse> tx = TransactionKt.sendTransaction(botClient, asset, transactionRecipient, trace, "");
        System.out.println(tx);

        try {
            TransactionKt.sendTransaction(botClient, asset, transactionRecipient, trace, "");
        } catch (SafeException e) {
            System.out.println("use same id should throw exception " + ExceptionsKt.stackTraceToString(e));
        }
    }

    private static void transactionToMultiple(HttpClient botClient) throws SafeException, IOException {
        String asset = StringExtensionKt.assetIdToAsset("965e5c6e-434c-3fa9-b780-c50f43cd955c"); // cnb
        MixAddress mixAddress = MixAddressKt.toMixAddress("MIXDLSoouhdcvedoiSzNHNRR4FNqVNwwgHUXkFoApTsz35fBHSNGyZEqGCzWuwDYrrWDwCXiaNcPec4C5cW8tCiE7BUHvs6A9YZ4B6FiFAEYY5Nd1etLA7aE7");
        assert mixAddress != null;
        TransactionRecipient transactionRecipient = new TransactionRecipient(mixAddress, "0.012", null, null);
        String trace = UUID.randomUUID().toString();
        System.out.println("trace: " + trace);
        List<TransactionResponse> tx = TransactionKt.sendTransaction(botClient, asset, transactionRecipient, trace, "");
        System.out.println(tx);
    }
}
