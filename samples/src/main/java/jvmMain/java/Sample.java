package jvmMain.java;

import kotlin.Unit;
import one.mixin.bot.HttpClient;
import one.mixin.bot.api.MixinResponse;
import one.mixin.bot.extension.Base64ExtensionKt;
import one.mixin.bot.safe.EdKeyPair;
import one.mixin.bot.util.ConversationUtil;
import one.mixin.bot.util.CryptoUtilKt;
import one.mixin.bot.vo.*;

import java.io.IOException;
import java.util.*;

import static jvmMain.java.Config.*;
import static one.mixin.bot.SessionKt.encryptPin;
import static one.mixin.bot.extension.Base64ExtensionKt.base64Encode;
import static one.mixin.bot.util.Base64UtilKt.base64Decode;
import static one.mixin.bot.util.CryptoUtilKt.*;

@SuppressWarnings("SameParameterValue")
public class Sample {

    final static String userPin = "131416";
    final static String CNB_assetId = "965e5c6e-434c-3fa9-b780-c50f43cd955c";
    final static String BTC_assetId = "c6d0c728-2624-429b-8e0d-d9d19b6592fa";
    final static String amount = "0.001";

    public static void main(String[] args) {
        EdKeyPair key = CryptoUtilKt.newKeyPairFromPrivateKey(Base64ExtensionKt.base64Decode(privateKey));
        byte[] pinToken = decryptPinToken(Base64ExtensionKt.base64Decode(pinTokenPem), key.getPrivateKey());
        HttpClient client = new HttpClient.Builder().configEdDSA(userId, sessionId, key.getPrivateKey(), null, null).enableDebug().enableAutoSwitch().build();
        try {
            utxo(client);
            EdKeyPair sessionKey = generateEd25519KeyPair();
            String sessionSecret = base64Encode(sessionKey.getPublicKey());

            // searchUser(client);

            getOutputs(client);

            User user = createUser(client, sessionSecret);
            assert user != null;

            // decrypt pin token
            byte[] userAesKey = calculateAgreement(Objects.requireNonNull(base64Decode(user.getPinToken())), privateKeyToCurve25519(sessionKey.getPrivateKey()));

            // get ticker
            getTicker(client);

            // get fiats
            getFiats(client);

            // get BTC fee
            getFee(client);


            HttpClient userClient = new HttpClient.Builder().configEdDSA(user.getUserId(), user.getSessionId(), sessionKey.getPrivateKey(), null, null).enableDebug().enableAutoSwitch().build();
            // create user's pin
            createPin(userClient, userAesKey);

            pinVerifyCall(userClient, userAesKey, userPin);

            // bot transfer to user
            transferToUser(client, user.getUserId(), pinToken, pin);

            Thread.sleep(2000);
            getAsset(userClient);

            // Create address
            String addressId = createAddress(userClient, userAesKey);

            // withdrawal
            withdrawalToAddress(userClient, addressId, userAesKey);

            // Delete address
            deleteAddress(userClient, addressId, userAesKey);

            // Send text message
            sendTextMessage(client, "639ec50a-d4f1-4135-8624-3c71189dcdcc", "Test message");

            createConversationAndSendMessage(client, userId);

            List<String> receivers = new ArrayList<>();
            receivers.add("00c5a4ae-dcdc-48db-ab8e-a7eef69b441d");
            receivers.add("087e91ff-7169-451a-aaaa-5b3297411a4b");
            receivers.add("105f6e8b-d249-4b4d-9beb-e03cefaebc37");
            transactions(client, receivers, pinToken, pin);

            transactionsOpponentKey(client, "XINQTmRReDuPEUAVEyDyE2mBgxa1ojVRAvpYcKs5nSA7FDBBfAEeVRn8s9vAm3Cn1qzQ7JtjG62go4jSJU6yWyRUKHpamWAM", pinToken, pin);

            networkSnapshot(client, "c8e73a02-b543-4100-bd7a-879ed4accdfc");
            networkSnapshots(client, CNB_assetId, null, 10, null);

            readGhostKey(client);
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void utxo(HttpClient client) throws IOException {
//        JsonObject response = client.getExternalService().getUtxoCall("b6afed179a8192513990e29953e3a6875eab53050b1e174d5c83ab76bbbd4b29",0).execute().body();
//        assert response != null;
//        System.out.printf("%s%n", Utxo.Companion.fromJson(response.getAsJsonObject("data")).getHash());
    }

    private static String createAddress(HttpClient client, byte[] userAesKey) throws IOException {
        MixinResponse<Address> addressResponse = client.getAddressService().createAddressesCall(new AddressRequest(Sample.CNB_assetId,
                "0x45315C1Fd776AF95898C77829f027AFc578f9C2B",
                null,
                "label",
                Objects.requireNonNull(encryptPin(
                        userAesKey,
                        Sample.userPin))
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

    private static void pinVerifyCall(HttpClient client,byte[] userAesKey,String pin) throws IOException{
        MixinResponse<User> pinResponse = client.getUserService().pinVerifyCall(new PinRequest(Objects.requireNonNull(encryptPin(userAesKey, pin)), null, null, null)).execute().body();
        if (pinResponse.isSuccess()) {
            System.out.printf("Pin verifyCall success %s%n", Objects.requireNonNull(pinResponse.getData()).getUserId());
        } else {
            System.out.println("Pin verifyCall error");
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
            System.out.println("Create user failure");
            return null;
        }
        assert user != null;

        return user;
    }

    private static void searchUser(HttpClient client) throws IOException {
        // Search user
        MixinResponse<User> userResponse = client.getUserService().searchCall("26832").execute().body();
        assert userResponse != null;
        if (userResponse.isSuccess()) {
            System.out.printf("User %s: %s%n", Objects.requireNonNull(userResponse.getData()).getFullName(), Objects.requireNonNull(userResponse.getData()).getUserId());
        } else {
            System.out.println("Search failure");
        }
    }

    private static void createPin(HttpClient client, byte[] userAesKey) throws IOException {
        MixinResponse<User> pinResponse = client.getUserService().createPinCall(new PinRequest(Objects.requireNonNull(encryptPin(userAesKey, Sample.userPin)), null, null, null)).execute().body();
        assert pinResponse != null;
        if (pinResponse.isSuccess()) {
            System.out.printf("Create pin success %s%n", Objects.requireNonNull(pinResponse.getData()).getUserId());
        } else {
            System.out.println("Create pin failure");
        }
    }

    private static void transferToUser(HttpClient client, String userId, byte[] aseKey, String pin) throws IOException {
        MixinResponse<Snapshot> transferResponse = client.getSnapshotService().transferCall(
                new TransferRequest(Sample.CNB_assetId, userId, Sample.amount, encryptPin(aseKey, pin, System.currentTimeMillis() * 1_000_000), null, null, null)).execute().body();
        assert transferResponse != null;
        if (transferResponse.isSuccess()) {
            System.out.printf("Transfer success: %s%n", Objects.requireNonNull(transferResponse.getData()).getSnapshotId());
        } else {
            System.out.println("Transfer failure");
        }
    }

    private static void getAsset(HttpClient client) throws IOException {
        // Get asset
        MixinResponse<Asset> assetResponse = client.getAssetService().getAssetCall(Sample.CNB_assetId).execute().body();
        assert assetResponse != null;
        if (assetResponse.isSuccess()) {
            System.out.printf("Assets %s: %s%n", Objects.requireNonNull(assetResponse.getData()).getSymbol(), Objects.requireNonNull(assetResponse.getData()).getBalance());
        } else {
            System.out.println("Transfer failure");
        }
    }

    private static void getOutputs(HttpClient client) throws IOException {
        // Get output
        MixinResponse<List<OutputResponse>> outputResponse = client.getUserService().multisigsOutputsCall(
                null, null, null, null, null, null
        ).execute().body();
        assert outputResponse != null;
        if (outputResponse.isSuccess()) {
            System.out.printf("Output: %d%n", Objects.requireNonNull(outputResponse.getData()).size());
        } else {
            System.out.println("Output failure");
        }
    }


    private static void withdrawalToAddress(HttpClient client, String addressId, byte[] userAesKey) throws IOException {
        MixinResponse<Snapshot> withdrawalsResponse = client.getSnapshotService().withdrawalsCall(new WithdrawalRequest(addressId, Sample.amount, Objects.requireNonNull(encryptPin(
                userAesKey,
                Sample.userPin
        )), UUID.randomUUID().toString(), "withdrawal test")).execute().body();
        assert withdrawalsResponse != null;
        if (withdrawalsResponse.isSuccess()) {
            addressId = Objects.requireNonNull(withdrawalsResponse.getData()).getSnapshotId();
            System.out.printf("Withdrawal success: %s%n", addressId);
        } else {
            System.out.println("Withdrawal failure");
        }
    }

    private static void deleteAddress(HttpClient client, String addressId, byte[] userAesKey) throws IOException {
        MixinResponse<Unit> deleteResponse = client.getAddressService().deleteCall(addressId, new Pin(Objects.requireNonNull(encryptPin(
                userAesKey,
                Sample.userPin
        )))).execute().body();
        assert deleteResponse != null;
        if (deleteResponse.isSuccess()) {
            System.out.printf("Delete success: %s%n", addressId);

        } else {
            System.out.println("Delete failure");
        }
    }

    private static void getFiats(HttpClient client) throws IOException {
        MixinResponse<List<Fiat>> fiatsResponse = client.getAssetService().getFiatsCall().execute().body();
        assert fiatsResponse != null;
        if (fiatsResponse.isSuccess()) {
            System.out.printf("Fiats success: %f%n", Objects.requireNonNull(fiatsResponse.getData()).get(0).getRate());
        } else {
            System.out.println("Fiats failure");
        }
    }

    private static void getFee(HttpClient client) throws IOException {
        MixinResponse<AssetFee> feeResponse = client.getAssetService().assetsFeeCall(BTC_assetId).execute().body();
        assert feeResponse != null;
        if (feeResponse.isSuccess()) {
            System.out.printf("Fee success: %s%n", Objects.requireNonNull(feeResponse.getData()).getAmount());
        } else {
            System.out.println("Fee failure");
        }
    }

    private static void getTicker(HttpClient client) throws IOException {
        MixinResponse<Ticker> tickerResponse = client.getAssetService().tickerCall(BTC_assetId, null).execute().body();
        assert tickerResponse != null;
        if (tickerResponse.isSuccess()) {
            System.out.printf("Ticker success: %s%n", Objects.requireNonNull(tickerResponse.getData()));
        } else {
            System.out.println("Ticker failure");
        }
    }

    private static void sendTextMessage(HttpClient client, String recipientId, String text) throws IOException {
        List<MessageRequest> messageRequests = new ArrayList<>();
        messageRequests.add(new MessageRequest(
                ConversationUtil.Companion.generateConversationId(userId, recipientId),
                recipientId, UUID.randomUUID().toString(), "PLAIN_TEXT",
                Base64.getEncoder().encodeToString(text.getBytes()), null, null
        ));
//        MixinResponse messageResponse = client.getMessageService().postMessageCall(messageRequests).execute().body();
//        assert messageResponse != null;
//        if (messageResponse.isSuccess()) {
//            System.out.println("Send success");
//        } else {
//            System.out.println("Send failure");
//        }
    }

    private static void transactions(HttpClient client, List<String> receivers, byte[] aseKey, String pin) throws IOException {
        MixinResponse<TransactionResponse> transactionResponse = client.getAssetService().transactionsCall(
                new TransactionRequest(Sample.CNB_assetId, new OpponentMultisig(
                        receivers,
                        2
                ), null, Sample.amount, encryptPin(aseKey, pin)
                        , null, null)).execute().body();
        assert transactionResponse != null;
        if (transactionResponse.isSuccess()) {
            System.out.printf("TransactionsResponse success: %s%n", Objects.requireNonNull(transactionResponse.getData()).getSnapshotId());
        } else {
            System.out.printf("Transactions failure: %s", Objects.requireNonNull(transactionResponse.getError()).getDescription());
        }
    }

    private static void transactionsOpponentKey(HttpClient client, String opponentKey, byte[] aseKey, String pin) throws IOException {
        MixinResponse<TransactionResponse> transactionResponse = client.getAssetService().transactionsCall(
                new TransactionRequest(Sample.CNB_assetId, null, opponentKey, Sample.amount, encryptPin(aseKey, pin)
                        , null, null)).execute().body();
        assert transactionResponse != null;
        if (transactionResponse.isSuccess()) {
            System.out.printf("TransactionsResponse success: %s%n", Objects.requireNonNull(transactionResponse.getData()).getTransactionHash());
        } else {
            System.out.printf("Transactions failure: %s", Objects.requireNonNull(transactionResponse.getError()).getDescription());
        }
    }


    private static void networkSnapshot(HttpClient client, String snapshotId) throws IOException {
        MixinResponse<NetworkSnapshot> snapshotResponse = client.getSnapshotService().networkSnapshotCall(snapshotId).execute().body();
        assert snapshotResponse != null;
        if (snapshotResponse.isSuccess()) {
            System.out.printf("Success: %s%n", Objects.requireNonNull(snapshotResponse.getData()).getSnapshotId());
        } else {
            System.out.printf("failure: %s", Objects.requireNonNull(snapshotResponse.getError()).getDescription());
        }
    }

    private static void networkSnapshots(HttpClient client, String assetId, String offset, int limit, String order) throws IOException {
        MixinResponse<List<NetworkSnapshot>> snapshotResponse = client.getSnapshotService().networkSnapshotsCall(assetId, offset, limit, order).execute().body();
        assert snapshotResponse != null;
        if (snapshotResponse.isSuccess()) {
            List<NetworkSnapshot> data = snapshotResponse.getData();
            System.out.printf("Success: %d%n", Objects.requireNonNull(snapshotResponse.getData()).size());
            for (NetworkSnapshot datum : data) {
                System.out.println(datum.toString());
            }
        } else {
            System.out.printf("failure: %s", Objects.requireNonNull(snapshotResponse.getError()).getDescription());
        }
    }

    private static void readGhostKey(HttpClient client) throws IOException {
        List<String> userList = new ArrayList<>();
        userList.add("639ec50a-d4f1-4135-8624-3c71189dcdcc");
        userList.add("d3bee23a-81d4-462e-902a-22dae9ef89ff");
        GhostKeyRequest request = new GhostKeyRequest(userList, 0, "");
        MixinResponse<GhostKey> response = client.getUserService().readGhostKeysCall(request).execute().body();
        assert response != null;

        if (response.isSuccess()) {
            System.out.printf("ReadGhostKey success %s%n", response.getData());
        } else {
            System.out.println("ReadGhostKey failure");
        }
    }

    private static void createConversationAndSendMessage(HttpClient client, String botUserId) throws IOException {
        List<ParticipantRequest> list = new ArrayList<>();
        ParticipantRequest botParticipant = new ParticipantRequest(botUserId, "", null);
        ParticipantRequest userParticipant = new ParticipantRequest("e26808d4-b31f-4e3b-9521-19e529b967b0", "", null);
        list.add(botParticipant);
        list.add(userParticipant);
        ConversationRequest conversationRequest = new ConversationRequest(UUID.randomUUID().toString(), "GROUP", "test group", null, null, list, null);
        MixinResponse<ConversationResponse> response = client.getConversationService().createCall(conversationRequest).execute().body();
        assert response != null;

        if (response.isSuccess()) {
            System.out.printf("create conversation success %s\n", response.getData());
        } else {
            System.out.printf("create conversation failure %s\n", response.getError());
            return;
        }

        ConversationResponse conversation = response.getData();
        if (conversation == null) return;

        MessageRequest messageRequest = new MessageRequest(
                conversation.getConversationId(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "PLAIN_TEXT",
                base64Encode("hello from bot".getBytes()), null, null);
        ArrayList<MessageRequest> l = new ArrayList<>();
        l.add(messageRequest);
        MixinResponse<Void> messageResponse = client.getMessageService().postMessageCall(l).execute().body();
        assert messageResponse != null;

        if (messageResponse.isSuccess()) {
            System.out.println("Bot send message success");
        } else {
            System.out.printf("Bot send message failure %s\n", messageResponse.getError());
        }
    }
}