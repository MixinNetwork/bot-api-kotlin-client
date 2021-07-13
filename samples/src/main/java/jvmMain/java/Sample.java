package jvmMain.java;

import kotlin.Unit;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import one.mixin.bot.HttpClient;
import one.mixin.bot.SessionToken;
import one.mixin.bot.api.MixinResponse;
import one.mixin.bot.util.ConversationUtil;
import one.mixin.bot.vo.*;

import java.io.IOException;
import java.security.KeyPair;
import java.util.*;

import static jvmMain.java.Config.*;
import static one.mixin.bot.SessionKt.encryptPin;
import static one.mixin.bot.extension.Base64ExtensionKt.base64Encode;
import static one.mixin.bot.util.Base64UtilKt.base64Decode;
import static one.mixin.bot.util.CryptoUtilKt.*;

public class Sample {

    final static String userPin = "131416";
    final static String CNB_assetId = "965e5c6e-434c-3fa9-b780-c50f43cd955c";
    final static String BTC_assetId = "c6d0c728-2624-429b-8e0d-d9d19b6592fa";
    final static String amount = "0.001";

    public static void main(String[] args) {
        EdDSAPrivateKey key = getEdDSAPrivateKeyFromString(privateKey);
        String pinToken = decryASEKey(pinTokenPem, key);
        HttpClient client = new HttpClient.Builder().configEdDSA(userId, sessionId, key).build();
        try {
            KeyPair sessionKey = generateEd25519KeyPair();
            EdDSAPublicKey publicKey = (EdDSAPublicKey) (sessionKey.getPublic());
            String sessionSecret = base64Encode(publicKey.getAbyte());

            User user = createUser(client, sessionSecret);
            assert user != null;
            client.setUserToken(getUserToken(user, sessionKey, false));

            // decrypt pin token
            String userAesKey;
            EdDSAPrivateKey userPrivateKey = (EdDSAPrivateKey) sessionKey.getPrivate();
            userAesKey = base64Encode(calculateAgreement(Objects.requireNonNull(base64Decode(user.getPinToken())), userPrivateKey));

            // get ticker
            getTicker(client);

            // get fiats
            getFiats(client);

            // get BTC fee
            getFee(client);

            // create user's pin
            createPin(client, userAesKey);

            //Use bot's token
            client.setUserToken(null);
            // bot transfer to user
            transferToUser(client, user.getUserId(), pinToken, pin);

            Thread.sleep(2000);
            // Use user's token
            client.setUserToken(getUserToken(user, sessionKey, false));
            getAsset(client);

            // Create address
            String addressId = createAddress(client, userAesKey);

            // withdrawal
            withdrawalToAddress(client, addressId, userAesKey);

            // Delete address
            deleteAddress(client, addressId, userAesKey);

            //Use bot's token
            client.setUserToken(null);
            // Send text message
            sendTextMessage(client, "639ec50a-d4f1-4135-8624-3c71189dcdcc", "Test message");

            List<String> receivers = new ArrayList<>();
            receivers.add("00c5a4ae-dcdc-48db-ab8e-a7eef69b441d");
            receivers.add("087e91ff-7169-451a-aaaa-5b3297411a4b");
            receivers.add("105f6e8b-d249-4b4d-9beb-e03cefaebc37");
            transactions(client, receivers, pinToken, pin);

            transactionsOpponentKey(client, "XINQTmRReDuPEUAVEyDyE2mBgxa1ojVRAvpYcKs5nSA7FDBBfAEeVRn8s9vAm3Cn1qzQ7JtjG62go4jSJU6yWyRUKHpamWAM", pinToken, pin);

            networkSnapshot(client, "c8e73a02-b543-4100-bd7a-879ed4accdfc");
            networkSnapshots(client, CNB_assetId);
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }


    private static String createAddress(HttpClient client, String userAesKey) throws IOException {
        MixinResponse<Address> addressResponse = client.getAddressService().createAddressesCall(new AddressRequest(Sample.CNB_assetId,
                "0x45315C1Fd776AF95898C77829f027AFc578f9C2B",
                null,
                "label",
                Objects.requireNonNull(encryptPin(
                        userAesKey,
                        Sample.userPin,
                        System.nanoTime()))
        )).execute().body();
        assert addressResponse != null;

        if (addressResponse.isSuccess()) {
            String addressId = Objects.requireNonNull(addressResponse.getData()).getAddressId();
            System.out.printf("Create address success: %s%n", addressId);
            return addressId;
        } else {
            return null;
        }
    }


    private static User createUser(HttpClient client, String sessionSecret) throws IOException {

        AccountRequest accountRequest = new AccountRequest(
                new Random().nextInt(10) + "User",
                sessionSecret
        );
        MixinResponse<User> userResponse = client.getUserService().createUsersCall(accountRequest).execute().body();
        assert userResponse != null;
        User user;
        if (userResponse.isSuccess()) {
            user = userResponse.getData();
            System.out.printf("Create user success: %s%n", Objects.requireNonNull(userResponse.getData()).getFullName());
        } else {
            System.out.println("Create user fail");
            return null;
        }
        assert user != null;

        return user;
    }

    private static void createPin(HttpClient client, String userAesKey) throws IOException {
        MixinResponse<User> pinResponse = client.getUserService().createPinCall(new PinRequest(Objects.requireNonNull(encryptPin(userAesKey, Sample.userPin, System.nanoTime())), null)).execute().body();
        assert pinResponse != null;
        if (pinResponse.isSuccess()) {
            System.out.printf("Create pin success %s%n", Objects.requireNonNull(pinResponse.getData()).getUserId());
        } else {
            System.out.println("Create pin fail");
        }
    }

    private static void transferToUser(HttpClient client, String userId, String aseKey, String pin) throws IOException {
        MixinResponse<Snapshot> transferResponse = client.getSnapshotService().transferCall(
                new TransferRequest(Sample.CNB_assetId, userId, Sample.amount, encryptPin(aseKey, pin, System.nanoTime())
                        , null, null, null)).execute().body();
        assert transferResponse != null;
        if (transferResponse.isSuccess()) {
            System.out.printf("Transfer success: %s%n", Objects.requireNonNull(transferResponse.getData()).getSnapshotId());
        } else {
            System.out.println("Transfer fail");
        }
    }

    private static void getAsset(HttpClient client) throws IOException {
        // Get asset
        MixinResponse<Asset> assetResponse = client.getAssetService().getAssetCall(Sample.CNB_assetId).execute().body();
        assert assetResponse != null;
        if (assetResponse.isSuccess()) {
            System.out.printf("Assets %s: %s%n", Objects.requireNonNull(assetResponse.getData()).getSymbol(), Objects.requireNonNull(assetResponse.getData()).getBalance());
        } else {
            System.out.println("Transfer fail");
        }
    }

    private static void withdrawalToAddress(HttpClient client, String addressId, String userAesKey) throws IOException {
        MixinResponse<Snapshot> withdrawalsResponse = client.getSnapshotService().withdrawalsCall(new WithdrawalRequest(addressId, Sample.amount, Objects.requireNonNull(encryptPin(
                userAesKey,
                Sample.userPin,
                System.nanoTime()
        )), UUID.randomUUID().toString(), "withdrawal test")).execute().body();
        assert withdrawalsResponse != null;
        if (withdrawalsResponse.isSuccess()) {
            addressId = Objects.requireNonNull(withdrawalsResponse.getData()).getSnapshotId();
            System.out.printf("Withdrawal success: %s%n", addressId);
        } else {
            System.out.println("Withdrawal fail");
        }
    }

    private static void deleteAddress(HttpClient client, String addressId, String userAesKey) throws IOException {
        MixinResponse<Unit> deleteResponse = client.getAddressService().deleteCall(addressId, new Pin(Objects.requireNonNull(encryptPin(
                userAesKey,
                Sample.userPin,
                System.nanoTime()
        )))).execute().body();
        assert deleteResponse != null;
        if (deleteResponse.isSuccess()) {
            System.out.printf("Delete success: %s%n", addressId);

        } else {
            System.out.println("Delete fail");
        }
    }

    private static void getFiats(HttpClient client) throws IOException {
        MixinResponse<List<Fiat>> fiatsResponse = client.getAssetService().getFiatsCall().execute().body();
        assert fiatsResponse != null;
        if (fiatsResponse.isSuccess()) {
            System.out.printf("Fiats success: %f%n", Objects.requireNonNull(fiatsResponse.getData()).get(0).getRate());
        } else {
            System.out.println("Fiats fail");
        }
    }

    private static void getFee(HttpClient client) throws IOException {
        MixinResponse<AssetFee> feeResponse = client.getAssetService().assetsFeeCall(BTC_assetId).execute().body();
        assert feeResponse != null;
        if (feeResponse.isSuccess()) {
            System.out.printf("Fee success: %s%n", Objects.requireNonNull(feeResponse.getData()).getAmount());
        } else {
            System.out.println("Fee fail");
        }
    }

    private static void getTicker(HttpClient client) throws IOException {
        MixinResponse<Ticker> tickerResponse = client.getAssetService().tickerCall(BTC_assetId, null).execute().body();
        assert tickerResponse != null;
        if (tickerResponse.isSuccess()) {
            System.out.printf("Ticker success: %s%n", Objects.requireNonNull(tickerResponse.getData()));
        } else {
            System.out.println("Ticker fail");
        }
    }

    private static void sendTextMessage(HttpClient client, String recipientId, String text) throws IOException {
        List<MessageRequest> messageRequests = new ArrayList<>();
        messageRequests.add(new MessageRequest(
                ConversationUtil.Companion.generateConversationId(userId, recipientId),
                recipientId, UUID.randomUUID().toString(), "PLAIN_TEXT",
                Base64.getEncoder().encodeToString(text.getBytes()), null, null
        ));
        MixinResponse messageResponse = client.getMessageService().postMessageCall(messageRequests).execute().body();
        assert messageResponse != null;
        if (messageResponse.isSuccess()) {
            System.out.println("Send success");
        } else {
            System.out.println("Send fail");
        }
    }

    private static SessionToken getUserToken(User user, KeyPair sessionKey, boolean isRsa) {
        if (isRsa) {
            return new SessionToken.RSA(user.getUserId(), user.getSessionId(), sessionKey.getPrivate());
        } else {
            return new SessionToken.EdDSA(user.getUserId(), user.getSessionId(),
                    base64Encode(((EdDSAPrivateKey) sessionKey.getPrivate()).getSeed()));
        }
    }


    private static void transactions(HttpClient client, List<String> receivers, String aseKey, String pin) throws IOException {
        MixinResponse<TransactionResponse> transactionResponse = client.getAssetService().transactionsCall(
                new TransactionRequest(Sample.CNB_assetId, new OpponentMultisig(
                        receivers,
                        2
                ), null, Sample.amount, encryptPin(aseKey, pin, System.nanoTime())
                        , null, null)).execute().body();
        assert transactionResponse != null;
        if (transactionResponse.isSuccess()) {
            System.out.printf("TransactionsResponse success: %s%n", Objects.requireNonNull(transactionResponse.getData()).getSnapshotId());
        } else {
            System.out.printf("Transactions fail: %s", Objects.requireNonNull(transactionResponse.getError()).getDescription());
        }
    }

    private static void transactionsOpponentKey(HttpClient client, String opponentKey, String aseKey, String pin) throws IOException {
        MixinResponse<TransactionResponse> transactionResponse = client.getAssetService().transactionsCall(
                new TransactionRequest(Sample.CNB_assetId, null, opponentKey, Sample.amount, encryptPin(aseKey, pin, System.nanoTime())
                        , null, null)).execute().body();
        assert transactionResponse != null;
        if (transactionResponse.isSuccess()) {
            System.out.printf("TransactionsResponse success: %s%n", Objects.requireNonNull(transactionResponse.getData()).getTransactionHash());
        } else {
            System.out.printf("Transactions fail: %s", Objects.requireNonNull(transactionResponse.getError()).getDescription());
        }
    }


    private static void networkSnapshot(HttpClient client, String snapshotId) throws IOException {
        MixinResponse<NetworkSnapshot> snapshotResponse = client.getSnapshotService().networkSnapshotCall(snapshotId).execute().body();
        assert snapshotResponse != null;
        if (snapshotResponse.isSuccess()) {
            System.out.printf("Success: %s%n", Objects.requireNonNull(snapshotResponse.getData()).getSnapshotId());
        } else {
            System.out.printf("Fail: %s", Objects.requireNonNull(snapshotResponse.getError()).getDescription());
        }
    }

    private static void networkSnapshots(HttpClient client, String assetId) throws IOException {
        MixinResponse<List<NetworkSnapshot>> snapshotResponse = client.getSnapshotService().networkSnapshotsCall(assetId, null, 10, "ASC").execute().body();
        assert snapshotResponse != null;
        if (snapshotResponse.isSuccess()) {
            System.out.printf("Success: %d%n", Objects.requireNonNull(snapshotResponse.getData()).size());
        } else {
            System.out.printf("Fail: %s", Objects.requireNonNull(snapshotResponse.getError()).getDescription());
        }
    }
}