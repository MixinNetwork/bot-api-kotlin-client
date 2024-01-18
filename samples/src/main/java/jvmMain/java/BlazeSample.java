package jvmMain.java;

import static jvmMain.java.Config.*;
import static one.mixin.bot.blaze.BlazeClientKt.sendTextMsg;

import java.io.IOException;

import okhttp3.WebSocket;
import one.mixin.bot.blaze.BlazeClient;
import one.mixin.bot.blaze.BlazeHandler;
import one.mixin.bot.blaze.BlazeMsg;
import one.mixin.bot.blaze.MsgData;
import one.mixin.bot.extension.ByteArrayExtensionKt;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
class BlazeSample {
  public static void main(String[] args) throws IOException {

    BlazeClient blazeClient =
        new BlazeClient.Builder()
            .configSafeUser(BOT_USER_ID, BOT_SESSION_ID, ByteArrayExtensionKt.hexStringToByteArray(BOT_SESSION_PRIVATE_KEY), null, null)
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
