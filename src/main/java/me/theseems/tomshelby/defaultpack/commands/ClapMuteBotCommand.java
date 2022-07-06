package me.theseems.tomshelby.defaultpack.commands;

import com.google.common.base.Joiner;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.AdminPermissibleBotCommand;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import me.theseems.tomshelby.defaultpack.punishment.ClapMutePunishment;
import me.theseems.tomshelby.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.temporal.ChronoUnit;

public class ClapMuteBotCommand extends SimpleBotCommand implements AdminPermissibleBotCommand {

  public ClapMuteBotCommand() {
    super(
        SimpleCommandMeta.onLabel("gag")
            .aliases("кляп", "clapmute", "muteclap")
            .description("Вставить кляп. Именно кляп! Особый вид мута"));
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

    if (dragResult.isSkipArg()) args = StringUtils.skipOne(args);

    if (args.length == 0) {
      bot.sendBack(
          update,
          SendMessage.builder()
              .text(
                  "Укажите нормальный срок присутствия кляпа во рту! (Целое положительно число длиной меньше 10)")
              .replyToMessageId(update.getMessage().getMessageId())
              .build());
      return;
    }

    int period;
    try {
      period = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      bot.sendBack(
          update,
          SendMessage.builder()
              .text(
                  "Укажите нормальный срок присутствия кляпа во рту! (Целое положительно число длиной меньше 10)")
              .replyToMessageId(update.getMessage().getMessageId())
              .build());
      return;
    }

    ChatMember chatMember = dragResult.getMember().get();
    bot.getPunishmentStorage()
        .addPunishment(
            chatMember.getUser().getId(),
            new ClapMutePunishment(
                period, ChronoUnit.SECONDS, Joiner.on(' ').join(StringUtils.skipOne(args))));

    bot.sendBack(
        update,
        SendMessage.builder()
            .chatId("")
            .text(
                update.getMessage().getFrom().getUserName()
                    + " вставил кляп в рот @"
                    + chatMember.getUser().getUserName()
                    + " на "
                    + period
                    + "c."
                    + (args.length > 1
                        ? " со словами '" + Joiner.on(' ').join(StringUtils.skipOne(args)) + "'"
                        : " При этом ничего не сказал..."))
            .build());
  }
}
