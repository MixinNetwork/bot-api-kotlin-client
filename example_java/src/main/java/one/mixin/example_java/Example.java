package one.mixin.example_java;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import one.mixin.bot.HttpClient;
import one.mixin.bot.SessionToken;
import one.mixin.bot.api.MixinResponse;
import one.mixin.bot.vo.*;

import java.util.Base64;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static one.mixin.bot.util.CryptoUtilKt.*;
import static one.mixin.bot.util.SessionKt.encryptPin;
import static one.mixin.example_java.Config.*;

public class Example {

    private static String base64Encode(byte[] src) {
        return Base64.getEncoder().encodeToString(src);
    }

    private static byte[] base64Encode(String src) {
        return Base64.getDecoder().decode(src);
    }

    public static void main(String[] args) {
        HttpClient client = new HttpClient(new SessionToken.EdDSA(userId, sessionId,
                base64Encode(privateKey.getSeed())), true);
        try {
            KeyPair sessionKey = generateEd25519KeyPair();
            EdDSAPublicKey publicKey = (EdDSAPublicKey) (sessionKey.getPublic());
            String sessionSecret = base64Encode(publicKey.getAbyte());

            AccountRequest accountRequest = new AccountRequest(
                    new Random().nextInt(10) + "User",
                    sessionSecret
            );
            MixinResponse<User> userResponse = client.getUserService().createUsersCall(accountRequest).execute().body();
            assert userResponse != null;
            User user;
            if (userResponse.isSuccess()) {
                user = userResponse.getData();
                System.out.println(Objects.requireNonNull(userResponse.getData()).getFullName());
            } else {
                return;
            }
            assert user != null;
            client.setUserToken(getUserToken(user, sessionKey, false));
            // decrypt pin token
            String userAesKey;

            EdDSAPrivateKey userPrivateKey = (EdDSAPrivateKey) sessionKey.getPrivate();
            userAesKey = base64Encode(calculateAgreement(base64Encode(user.getPinToken()), userPrivateKey));

            MixinResponse<User> pinResponse = client.getUserService().createPinCall(new PinRequest(Objects.requireNonNull(encryptPin(userAesKey, System.nanoTime(), "131416")), null)).execute().body();
            assert pinResponse != null;
            if (pinResponse.isSuccess()) {
                System.out.println(Objects.requireNonNull(pinResponse.getData()).getUserId());
            } else {
                return;
            }

            // bot transfer to user
            client.setUserToken(null);
            MixinResponse<Snapshot> transferResponse = client.getAssetService().transferCall(
                    new TransferRequest("965e5c6e-434c-3fa9-b780-c50f43cd955c", user.getUserId(), "2", encryptPin(
                            pinToken,
                            System.nanoTime(),
                            pin
                    ), null, null, null)).execute().body();
            assert transferResponse != null;
            if (transferResponse.isSuccess()) {
                System.out.println(Objects.requireNonNull(transferResponse.getData()).getSnapshotId());
            } else {
                return;
            }
            // Use user's token
            client.setUserToken(getUserToken(user, sessionKey, false));
            Thread.sleep(2000);
            // Get asset
            MixinResponse<Asset> assetResponse = client.getAssetService().getAssetCall("965e5c6e-434c-3fa9-b780-c50f43cd955c").execute().body();
            assert assetResponse != null;
            if (assetResponse.isSuccess()) {
                System.out.println(Objects.requireNonNull(assetResponse.getData()).getBalance());
            } else {
                return;
            }

            // Create address
            MixinResponse<Address> addressResponse = client.getAssetService().createAddressesCall(new AddressesRequest("965e5c6e-434c-3fa9-b780-c50f43cd955c",
                    "0x45315C1Fd776AF95898C77829f027AFc578f9C2B",
                    "label", Objects.requireNonNull(encryptPin(
                    userAesKey,
                    System.nanoTime(),
                    "131416"
            )), null, null
            )).execute().body();
            assert addressResponse != null;
            String addressId;
            if (addressResponse.isSuccess()) {
                addressId = Objects.requireNonNull(addressResponse.getData()).getAddressId();
                System.out.println(addressId);
            } else {
                return;
            }

            // withdrawal
            MixinResponse<Snapshot> withdrawalsResponse = client.getAssetService().withdrawalsCall(new WithdrawalRequest(addressId, "2", Objects.requireNonNull(encryptPin(
                    userAesKey,
                    System.nanoTime(),
                    "131416"
            )), UUID.randomUUID().toString(), "withdrawal test")).execute().body();
            assert withdrawalsResponse != null;
            if (withdrawalsResponse.isSuccess()) {
                addressId = Objects.requireNonNull(withdrawalsResponse.getData()).getSnapshotId();
                System.out.println(addressId);
            }
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
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
}