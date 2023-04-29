package jvmMain.java;

import static jvmMain.java.Config.*;
import static one.mixin.bot.blaze.BlazeClientKt.sendTextMsg;
import static one.mixin.bot.util.CryptoUtilKt.getEdDSAPrivateKeyFromString;

import java.io.IOException;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import okhttp3.WebSocket;
import one.mixin.bot.blaze.BlazeClient;
import one.mixin.bot.blaze.BlazeHandler;
import one.mixin.bot.blaze.BlazeMsg;
import one.mixin.bot.blaze.MsgData;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public class Blaze {
  public static void main(String[] args) throws IOException {
    EdDSAPrivateKey key = getEdDSAPrivateKeyFromString(privateKey);
    BlazeClient blazeClient =
        new BlazeClient.Builder()
            .configEdDSA(userId, sessionId, key)
            .enableDebug()
            .enableParseData()
            .enableAutoAck()
            .blazeHandler(new MyBlazeHandler())
            .build();
    blazeClient.start();

    // 卡一下；因为没有任何别的服务启动
    System.in.read();
  }

  public static class MyBlazeHandler implements BlazeHandler {

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
