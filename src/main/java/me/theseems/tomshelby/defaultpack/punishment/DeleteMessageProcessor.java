package me.theseems.tomshelby.defaultpack.punishment;

import me.theseems.tomshelby.Main;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.punishment.Punishment;
import me.theseems.tomshelby.punishment.PunishmentProcessor;
import me.theseems.tomshelby.punishment.PunishmentType;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DeleteMessageProcessor implements PunishmentProcessor {
  @Override
  public boolean handle(Update update, Punishment punishment) {
    ThomasBot bot = Main.getBot();
    try {
      bot.execute(
          new DeleteMessage()
              .setChatId(update.getMessage().getChatId())
              .setMessageId(update.getMessage().getMessageId()));
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }

    return false;
  }

  @Override
  public PunishmentType getType() {
    return PunishmentType.MUTE;
  }

  @Override
  public String getName() {
    return "DeleteMessage";
  }

  @Override
  public int getPriority() {
    return 1000;
  }
}
