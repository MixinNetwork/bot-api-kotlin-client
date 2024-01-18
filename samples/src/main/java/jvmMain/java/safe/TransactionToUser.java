package jvmMain.java.safe;

import jvmMain.java.Common;
import one.mixin.bot.safe.TransactionKt;
import one.mixin.bot.vo.safe.TransactionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionToUser {
    public static void main(String[] args) {
        String traceId = UUID.randomUUID().toString();
        try {
            ArrayList<String> receivers = new ArrayList<>();
            receivers.add("cfb018b0-eaf7-40ec-9e07-28a5158f1269");
            List<TransactionResponse> transactions = TransactionKt.sendTransactionToUser(
                    Common.botClient,
                    Common.Token.CNB.getAssetId(),
                    receivers,
                    "0.0010",
                    "test from java",
                    traceId
            );
            for (TransactionResponse transaction : transactions) {
                System.out.println("view in view transaction in: https://viewblock.io/mixin/tx/" + transaction.getTransactionHash());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
