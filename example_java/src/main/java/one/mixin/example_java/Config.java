package one.mixin.example_java;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import java.util.Base64;
import static one.mixin.bot.util.CryptoUtilKt.calculateAgreement;
import static one.mixin.bot.util.CryptoUtilKt.getEdDSAPrivateKeyFromString;

final class Config {
    final static String pin = "912973";
    final static String userId = "d066f2d2-1a91-416b-9241-f3547d99a753";
    final static String sessionId = "aaf0a16f-55bf-4c98-a6d5-c633de812a9d";
    final static EdDSAPrivateKey privateKey = getEdDSAPrivateKeyFromString(
            "RNlQH88Odw0GbSgIWJWlavyJAhOOjZ4L_Mx40T92mZgJUH6LmhFwgLpTied_HUsV7SBpAnzlVtrGk_LYPwn4KA");

    final static String pinToken;

    static {
        pinToken = Base64.getEncoder().encodeToString((calculateAgreement(Base64.getUrlDecoder().decode("wyhoW8tRu5MRl4xubpd_uV5KsT6xbVYK5C8P8NLeUHk"), privateKey)));
        assert pinToken != null;
    }
}
