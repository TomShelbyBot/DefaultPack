package me.theseems.tomshelby.defaultpack.commands;

import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import me.theseems.tomshelby.punishment.Punishment;
import me.theseems.tomshelby.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;

public class CheckPunishmentsBotCommand extends SimpleBotCommand {
  public CheckPunishmentsBotCommand() {
    super(new SimpleCommandMeta().label("pcheck").description("Проверить наказания"));
  }

  @Override
  public void handle(ThomasBot bot, String[] args, Update update) {
    StringUtils.DragResult dragResult = StringUtils.dragFrom(update, args);
    if (!dragResult.getMember().isPresent()) {
      bot.sendBack(
          update,
          SendMessage.builder()
              .text("Не могу найти гражданина которому нужно вставить кляп!")
              .replyToMessageId(update.getMessage().getMessageId())
              .build());
      return;
    }

    ChatMember chatMember = dragResult.getMember().get();

    Collection<Punishment> punishments =
        bot.getPunishmentStorage().getPunishments(chatMember.getUser().getId());
    StringBuilder builder = new StringBuilder();

    builder.append(
        punishments.isEmpty()
            ? "У юзера нет активных наказаний"
            : "У юзера есть " + punishments.size() + " активных наказаний");
    for (Punishment punishment : punishments) {
      builder.append("\n").append(punishment.getType().name());
      builder
          .append("\n")
          .append("Причина: ")
          .append(punishment.getReason().isPresent() ? punishment.getReason().get() : "<нет>");
    }

    bot.sendBack(update, SendMessage.builder().chatId("").text(builder.toString()).build());
  }
}
