package one.mixin.example_java;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import one.mixin.bot.util.Base64;

import java.io.IOException;

import static one.mixin.bot.util.CryptoUtilKt.calculateAgreement;
import static one.mixin.bot.util.CryptoUtilKt.getEdDSAPrivateKeyFromString;

class Config {
    static String pin = "789222";
    static String userId ="5fa13575-f078-4c92-b913-fdfd1611f573";
    static String sessionId = "f14c65b0-1972-464f-96d4-bd93e7650c1d";
    static EdDSAPrivateKey privateKey = getEdDSAPrivateKeyFromString(
            "PJyrEh0N9Toe8fXe5OfrxdTSZE2U-636IjigGRANNqBQJ9W1xV251IXQIvVwMWW4zBvqH4A7xhotM2_2X-_aLQ");
    static String pinToken = "wOv6Epc0TXcQOWYqjhJnNNc1iAfyfXxbauDZ8XSQPzQ";

}
