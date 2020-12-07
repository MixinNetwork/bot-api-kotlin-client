package one.mixin.example_java;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import one.mixin.bot.HttpClient;
import one.mixin.bot.TokenInfo;
import one.mixin.bot.api.MixinResponse;
import one.mixin.bot.util.Base64;
import one.mixin.bot.vo.*;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static one.mixin.bot.util.CryptoUtilKt.*;
import static one.mixin.bot.util.SessionKt.encryptPin;
import static one.mixin.example_java.Config.*;

public class Example {

    public static void main(String[] args) {
        HttpClient client = new HttpClient(userId, sessionId, privateKey, false);
        try {
            boolean isRsa = false; // 是否使用RSA Key 推荐false 使用EdDSA

            // create user 将用户注册到 Mixin 网络
            KeyPair sessionKey;
            if (isRsa) {
                sessionKey = generateRSAKeyPair(2048);
            } else {
                sessionKey = generateEd25519KeyPair();
            }
            String sessionSecret;
            if (isRsa) {
                sessionSecret = Base64.encodeBytes(sessionKey.getPublic().getEncoded());
            } else {
                EdDSAPublicKey publicKey = (EdDSAPublicKey) (sessionKey.getPublic());
                sessionSecret = Base64.encodeBytes(publicKey.getAbyte());
            }
            AccountRequest accountRequest = new AccountRequest(
                    new Random().nextInt(10) + "User",
                    sessionSecret
            );
            // create user 将用户注册到 Mixin 网络
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
            SecretPinIterator pinIterator = new SecretPinIterator();
            client.setUserToken(getUserToken(user, sessionKey, isRsa));
            // decrypt pin token
            String userAesKey;
             if (isRsa) {
                 userAesKey= rsaDecrypt(sessionKey.getPrivate(), user.getSessionId(), user.getPinToken());
            } else {
                 EdDSAPrivateKey privateKey =(EdDSAPrivateKey) sessionKey.getPrivate();
                 userAesKey = Base64.encodeBytes(calculateAgreement(Base64.decode(user.getPinToken()), privateKey));
            }

            MixinResponse<User> pinResponse = client.getUserService().createPinCall(new PinRequest(Objects.requireNonNull(encryptPin(pinIterator, userAesKey, "131416")), null)).execute().body();
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
                            pinIterator,
                            pinToken,
                            pin
                    ), null, null, null)).execute().body();
            assert transferResponse != null;
            if (transferResponse.isSuccess()) {
                System.out.println(Objects.requireNonNull(transferResponse.getData()).getSnapshotId());
            } else {
                return;
            }
            // Use user's token
            client.setUserToken(getUserToken(user, sessionKey, isRsa));
            Thread.sleep(2000);
            // Get asset 获取asset
            MixinResponse<Asset> assetResponse = client.getAssetService().getAssetCall("965e5c6e-434c-3fa9-b780-c50f43cd955c").execute().body();
            assert assetResponse != null;
            if (assetResponse.isSuccess()) {
                System.out.println(Objects.requireNonNull(assetResponse.getData()).getBalance());
            } else {
                return;
            }

            // Create address 创建地址
            MixinResponse<Address> addressResponse = client.getAssetService().createAddressesCall(new AddressesRequest("965e5c6e-434c-3fa9-b780-c50f43cd955c",
                    "0x45315C1Fd776AF95898C77829f027AFc578f9C2B",
                    "label", Objects.requireNonNull(encryptPin(
                    pinIterator,
                    userAesKey,
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

            // withdrawal 提现到地址
            MixinResponse<Snapshot> withdrawalsResponse = client.getAssetService().withdrawalsCall(new WithdrawalRequest(addressId, "2", Objects.requireNonNull(encryptPin(
                    pinIterator,
                    userAesKey,
                    "131416"
            )), UUID.randomUUID().toString(), "withdrawal test")).execute().body();
            if (withdrawalsResponse.isSuccess()) {
                addressId = Objects.requireNonNull(withdrawalsResponse.getData()).getSnapshotId();
                System.out.println(addressId);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    private static TokenInfo getUserToken(User user, KeyPair sessionKey, boolean isRsa) {
        if (isRsa) {
            return new TokenInfo.RSA(user.getUserId(), user.getSessionId(), sessionKey.getPrivate());
        } else {
            return new TokenInfo.EdDSA(user.getUserId(), user.getSessionId(),
                    Base64.encodeBytes(((EdDSAPrivateKey) sessionKey.getPrivate()).getSeed()));
        }
    }
}