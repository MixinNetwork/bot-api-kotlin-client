package jvmMain.java.legacy;

import jvmMain.java.Common;
import jvmMain.java.Config;
import one.mixin.bot.SessionKt;
import one.mixin.bot.api.MixinResponse;
import one.mixin.bot.extension.ByteArrayExtensionKt;
import one.mixin.bot.safe.TipBody;
import one.mixin.bot.util.CryptoUtilKt;
import one.mixin.bot.vo.Snapshot;
import one.mixin.bot.vo.TransferRequest;

import java.io.IOException;
import java.util.UUID;

public class LegacyTransferToUser {
    public static void main(String[] args) throws IOException {

        byte[] serverPublicKey = ByteArrayExtensionKt.hexStringToByteArray(Config.BOT_SERVER_PUBLIC_KEY);
        byte[] sessionPrivateKey = ByteArrayExtensionKt.hexStringToByteArray(Config.BOT_SESSION_PRIVATE_KEY);
        byte[] pinToken = CryptoUtilKt.decryptPinToken(CryptoUtilKt.publicKeyToCurve25519(serverPublicKey), sessionPrivateKey);

        final String assetId = Common.Token.CNB.getAssetId();
        final String amount = "0.00000001";
        final String traceId = UUID.randomUUID().toString();
        final String memo = "Hello mixin java";
        final String userId = "cfb018b0-eaf7-40ec-9e07-28a5158f1269";

        byte[] signTarget = TipBody.forTransfer(assetId, userId, amount, traceId, memo);
        String pin = SessionKt.encryptTipPin(pinToken, signTarget, ByteArrayExtensionKt.hexStringToByteArray(Config.BOT_SPEND_KEY));

        MixinResponse<Snapshot> response = Common.botClient.getSnapshotService().transferCall(new TransferRequest(
                assetId, userId, amount, pin, traceId, memo, null
        )).execute().body();
        System.out.println(response);
    }

}
