package me.theseems.tomshelby.defaultpack.handlers;

import me.theseems.tomshelby.Main;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.update.SimpleUpdateHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class WelcomeHandler extends SimpleUpdateHandler {
  @Override
  public boolean handleUpdate(ThomasBot bot, Update update) {
    if (!update.hasMessage()) return true;

    Message message = update.getMessage();
    if (!bot.getChatStorage()
        .lookup(message.getChatId().toString(), message.getFrom().getUserName())
        .isPresent()) {
      bot.getChatStorage()
          .put(message.getChatId().toString(), message.getFrom().getUserName(), message.getFrom().getId());
      bot.sendBack(
          update,
          SendMessage.builder()
              .chatId("")
              .text("Привет, " + message.getFrom().getUserName() + "! Приятно познакомиться :)")
              .build());
      Main.save();
    }

    return true;
  }
}
