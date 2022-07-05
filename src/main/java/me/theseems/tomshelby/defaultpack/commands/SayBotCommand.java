package me.theseems.tomshelby.defaultpack.commands;

import com.google.common.base.Joiner;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SayBotCommand extends SimpleBotCommand {
  public SayBotCommand() {
    super(SimpleCommandMeta.onLabel("say").description("Сказать что-то от имени бота"));
  }

  @Override
  public void handle(ThomasBot bot, String[] args, Update update) {
    if (args.length == 0) {
      bot.sendBack(
          update,
          SendMessage.builder()
              .replyToMessageId(update.getMessage().getMessageId())
              .text("А что сказать-то?")
              .build());
      return;
    }

    try {
      bot.execute(
          DeleteMessage.builder()
              .chatId(update.getMessage().getChatId().toString())
              .messageId(update.getMessage().getMessageId())
              .build());
      bot.sendBack(update, SendMessage.builder().chatId("").text(Joiner.on(' ').join(args)).build());
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
}
