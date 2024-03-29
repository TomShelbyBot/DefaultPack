package me.theseems.tomshelby.defaultpack.commands;

import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.AdminPermissibleBotCommand;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import me.theseems.tomshelby.storage.TomMeta;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class NoStickerBotCommand extends SimpleBotCommand implements AdminPermissibleBotCommand {
  public static final String STICKER_MODE_META_KEY = "stickerMode";

  public NoStickerBotCommand() {
    super(
        SimpleCommandMeta.onLabel("stickermode")
            .aliases("sticks", "togglest")
            .description("Запрещать извергам слать стикеры."));
  }

  @Override
  public void handle(ThomasBot bot, String[] args, Update update) {
    TomMeta meta = bot.getChatStorage().getChatMeta(update.getMessage().getChatId().toString());
    boolean current = meta.getBoolean(STICKER_MODE_META_KEY).orElse(false);
    meta.set(STICKER_MODE_META_KEY, !current);

    String statusText = (!current ? "_ВКЛЮЧЕН_" : "выключен");
    SendMessage sendMessage =
            SendMessage.builder()
                    .chatId("")
                    .text("*Режим блокировки стикеров*: " + statusText)
                    .build();

    sendMessage.enableMarkdown(true);
    bot.sendBack(update, sendMessage);
  }
}
