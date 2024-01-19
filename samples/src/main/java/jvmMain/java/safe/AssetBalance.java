package jvmMain.java.safe;

import jvmMain.java.Common;
import jvmMain.kotlin.Config;
import one.mixin.bot.safe.OutputKt;

import java.util.ArrayList;

public class AssetBalance {

    public static void main(String[] args) {
        ArrayList<String> members = new ArrayList<>();
        members.add(Config.BOT_USER_ID);
        String assetBalance = OutputKt.assetBalance(Common.botClient, Common.Token.CNB.getAssetId(), members);
        System.out.println("assetBalance: " + assetBalance);
    }
}
