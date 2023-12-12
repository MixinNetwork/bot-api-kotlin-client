package jvmMain.java;

import static jvmMain.java.Config.*;
import static one.mixin.bot.blaze.BlazeClientKt.sendTextMsg;

import java.io.IOException;
import okhttp3.WebSocket;
import one.mixin.bot.blaze.BlazeClient;
import one.mixin.bot.blaze.BlazeHandler;
import one.mixin.bot.blaze.BlazeMsg;
import one.mixin.bot.blaze.MsgData;
import one.mixin.bot.extension.Base64ExtensionKt;
import one.mixin.bot.safe.EdKeyPair;
import one.mixin.bot.util.CryptoUtilKt;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
class BlazeSample {
  public static void main(String[] args) throws IOException {
    EdKeyPair key = CryptoUtilKt.newKeyPairFromPrivateKey(Base64ExtensionKt.base64Decode(privateKey));
    BlazeClient blazeClient =
        new BlazeClient.Builder()
            .configEdDSA(userId, sessionId, key.getPrivateKey(), null, null)
            .enableDebug()
            .enableParseData()
            .enableAutoAck()
            .blazeHandler(new MyBlazeHandler())
            .build();
    blazeClient.start();

    // 卡一下；因为没有任何别的服务启动
    //noinspection ResultOfMethodCallIgnored
    System.in.read();
  }

  private static class MyBlazeHandler implements BlazeHandler {

    @Override
    public boolean onMessage(@NotNull WebSocket webSocket, @NotNull BlazeMsg blazeMsg) {
      System.out.println(blazeMsg);
      MsgData data = blazeMsg.getData();
      if (data != null) {
        sendTextMsg(webSocket, data.getConversionId(), data.getUserId(), "read");
      }

      return true;
    }
  }
}
