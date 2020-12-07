package one.mixin.example_java;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import one.mixin.bot.util.Base64;

import java.io.IOException;

import static one.mixin.bot.util.CryptoUtilKt.calculateAgreement;
import static one.mixin.bot.util.CryptoUtilKt.getEdDSAPrivateKeyFromString;

class Config {
    static String pin = "674239";
    static String userId = "d066f2d2-1a91-416b-9241-f3547d99a753";
    static String sessionId = "b36e2814-9702-4a30-9bca-361b46541d16";
    static EdDSAPrivateKey privateKey = getEdDSAPrivateKeyFromString(
            "6-gv2fSIEUjU_LDsr-oghQ4uBkYZ45iKSpUsQW_tVjAbM8COR2d5ByTiJZ_dVhvg-krY9ljcrcO6Xoi_cqs2BQ");

    static String pinToken;

    static {
        try {
            pinToken = Base64.encodeBytes(calculateAgreement(Base64.decodeWithoutPadding("-zz-URVGh0a_appbXrUWmw-6UMfJzfAfkltFbZ23oxs"), privateKey));
            assert pinToken !=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
