package jvmMain.java;

import one.mixin.bot.HttpClient;
import one.mixin.bot.extension.ByteArrayExtensionKt;

public class Common {

    public static final HttpClient botClient = new HttpClient.Builder().configSafeUser(
            Config.BOT_USER_ID,
            Config.BOT_SESSION_ID,
            ByteArrayExtensionKt.hexStringToByteArray(Config.BOT_SESSION_PRIVATE_KEY),
            null,
            ByteArrayExtensionKt.hexStringToByteArray(Config.BOT_SPEND_KEY)
    ).enableDebug().build();

    public enum Token {
        CNB("965e5c6e-434c-3fa9-b780-c50f43cd955c"),
        TRON_USDT("b91e18ff-a9ae-3dc7-8679-e935d9a4b34b"),
        TRX("25dabac5-056a-48ff-b9f9-f67395dc407c");

        private final String assetId;

        Token(String assetId) {
            this.assetId = assetId;
        }

        public String getAssetId() {
            return assetId;
        }
    }


}

