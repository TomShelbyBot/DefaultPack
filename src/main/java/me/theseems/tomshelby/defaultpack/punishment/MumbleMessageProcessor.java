package me.theseems.tomshelby.defaultpack.punishment;

import me.theseems.tomshelby.Main;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.punishment.Punishment;
import me.theseems.tomshelby.punishment.PunishmentProcessor;
import me.theseems.tomshelby.punishment.PunishmentType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MumbleMessageProcessor implements PunishmentProcessor {
  private final Map<Long, Instant> latest;

  public MumbleMessageProcessor() {
    latest = new ConcurrentHashMap<>();
  }

  @Override
  public boolean handle(Update update, Punishment punishment) {
    ThomasBot bot = Main.getBot();
    try {

      bot.execute(
          DeleteMessage.builder()
              .chatId(update.getMessage().getChatId().toString())
              .messageId(update.getMessage().getMessageId())
              .build());

      if (!latest.containsKey(update.getMessage().getFrom().getId())
          || latest.get(update.getMessage().getFrom().getId()).isBefore(new Date().toInstant())) {
        int length = update.getMessage().hasText() ? update.getMessage().getText().length() : 1;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
          builder.append(i % 2 == 0 ? "у" : "м");
        }

        bot.sendBack(
            update,
            SendMessage.builder()
                .text(
                    "Кажется, @"
                        + update.getMessage().getFrom().getUserName()
                        + " пытался сказать что-то: "
                        + builder.toString())
                .build());

        latest.put(
            update.getMessage().getFrom().getId(),
            new Date().toInstant().plus(10, ChronoUnit.SECONDS));
      }
    } catch (TelegramApiException e) {
      bot.sendBack(
          update,
          SendMessage.builder()
              .chatId("")
              .text("Кляп вылетает!!!")
              .replyToMessageId(update.getMessage().getMessageId())
              .build());
    }

    return false;
  }

  @Override
  public PunishmentType getType() {
    return PunishmentType.CLAP_MUTE;
  }

  @Override
  public String getName() {
    return "MumbleMessage";
  }

  @Override
  public int getPriority() {
    return 1000;
  }
}
