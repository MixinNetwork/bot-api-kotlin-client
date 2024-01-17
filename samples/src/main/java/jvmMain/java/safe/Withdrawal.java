package jvmMain.java.safe;

import jvmMain.java.Common;
import jvmMain.kotlin.Config;
import one.mixin.bot.safe.SafeException;
import one.mixin.bot.safe.TransactionKt;
import one.mixin.bot.vo.safe.TransactionResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Withdrawal {
    public static void main(String[] args) {
        String traceId = UUID.randomUUID().toString();
        try {
            List<TransactionResponse> transactions = TransactionKt.withdrawalToAddress(
                    Common.botClient,
                    Common.Token.TRON_USDT.getAssetId(),
                    "TQ5NMqJjhpQGK7YJbESKfjXZoQXf2hZL7r",
                    null,
                    "0.0001",
                    null,
                    traceId
            );
            for (TransactionResponse transaction : transactions) {
                System.out.println("view in view transaction in: https://viewblock.io/mixin/tx/" + transaction.getTransactionHash());
            }
        } catch (SafeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
