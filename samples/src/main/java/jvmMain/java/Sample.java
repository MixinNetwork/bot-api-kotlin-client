package jvmMain.java;

import one.mixin.bot.HttpClient;
import one.mixin.bot.api.MixinResponse;
import one.mixin.bot.extension.ByteArrayExtensionKt;
import one.mixin.bot.safe.EdKeyPair;
import one.mixin.bot.util.ConversationUtil;
import one.mixin.bot.vo.*;

import java.io.IOException;
import java.util.*;

import static jvmMain.java.Config.*;
import static one.mixin.bot.SessionKt.encryptPin;
import static one.mixin.bot.extension.Base64ExtensionKt.base64Decode;
import static one.mixin.bot.extension.Base64ExtensionKt.base64Encode;
import static one.mixin.bot.util.CryptoUtilKt.*;

@Deprecated
@SuppressWarnings("SameParameterValue")
public class Sample {

    final static String userPin = "131416";
    final static String CNB_assetId = "965e5c6e-434c-3fa9-b780-c50f43cd955c";
    final static String BTC_assetId = "c6d0c728-2624-429b-8e0d-d9d19b6592fa";
    final static String amount = "0.001";

    public static void main(String[] args) {
        HttpClient client = new HttpClient.Builder().configSafeUser(BOT_USER_ID, BOT_SESSION_ID,
                ByteArrayExtensionKt.hexStringToByteArray(BOT_SESSION_PRIVATE_KEY), null, null).enableDebug().build();
        try {
            EdKeyPair sessionKey = generateEd25519KeyPair();
            String sessionSecret = base64Encode(sessionKey.getPublicKey());

            // searchUser(client);

            User user = createUser(client, sessionSecret);
            assert user != null;

            // decrypt pin token
            byte[] userAesKey = calculateAgreement(Objects.requireNonNull(base64Decode(user.getPinToken())), privateKeyToCurve25519(sessionKey.getPrivateKey()));


            HttpClient userClient = new HttpClient.Builder().configSafeUser(user.getUserId(), user.getSessionId(), sessionKey.getPrivateKey(), null, null).enableDebug().build();
            // create user's pin
            createPin(userClient, userAesKey);

            pinVerifyCall(userClient, userAesKey, userPin);

            Thread.sleep(2000);

            // Send text message
            sendTextMessage(client, "639ec50a-d4f1-4135-8624-3c71189dcdcc", "Test message");

            createConversationAndSendMessage(client, BOT_USER_ID);

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }


    private static void pinVerifyCall(HttpClient client, byte[] userAesKey, String pin) throws IOException {
        MixinResponse<User> pinResponse = client.getUserService().pinVerifyCall(new PinRequest(Objects.requireNonNull(encryptPin(userAesKey, pin)), null, null)).execute().body();
        if (pinResponse.isSuccess()) {
            System.out.printf("Pin verifyCall success %s%n", Objects.requireNonNull(pinResponse.getData()).getUserId());
        } else {
            System.out.println("Pin verifyCall error");
        }
    }

    static User createUser(HttpClient client, String sessionSecret) throws IOException {

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

    static void createPin(HttpClient client, byte[] userAesKey) throws IOException {
        MixinResponse<User> pinResponse = client.getUserService().createPinCall(new PinRequest(Objects.requireNonNull(encryptPin(userAesKey, Sample.userPin)), null, null)).execute().body();
        assert pinResponse != null;
        if (pinResponse.isSuccess()) {
            System.out.printf("Create pin success %s%n", Objects.requireNonNull(pinResponse.getData()).getUserId());
        } else {
            System.out.println("Create pin failure");
        }
    }


    private static void sendTextMessage(HttpClient client, String recipientId, String text) throws IOException {
        List<MessageRequest> messageRequests = new ArrayList<>();
        messageRequests.add(new MessageRequest(
                ConversationUtil.Companion.generateConversationId(BOT_USER_ID, recipientId),
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