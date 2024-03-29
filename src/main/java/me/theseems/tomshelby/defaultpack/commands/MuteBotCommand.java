package me.theseems.tomshelby.defaultpack.commands;

import com.google.common.base.Joiner;
import me.theseems.tomshelby.Main;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.AdminPermissibleBotCommand;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import me.theseems.tomshelby.defaultpack.punishment.MutePunishment;
import me.theseems.tomshelby.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class MuteBotCommand extends SimpleBotCommand implements AdminPermissibleBotCommand {
  public MuteBotCommand() {
    super(
        SimpleCommandMeta.onLabel("mute")
            .description("Под угрозой расстрела сообещния запретить человеку писать."));
  }

  /**
   * Handle update for that command
   *
   * @param bot to handle with
   * @param update to handle
   */
  @Override
  public void handle(ThomasBot bot, String[] args, Update update) {
    if (args.length == 0) {
      bot.sendBack(
          update, SendMessage.builder().text("Укажите юзера кому нужно выдать пизды (мут)!").build());
    } else {

      if (args[0].startsWith("@")) args[0] = args[0].substring(1);

      int period = 5;
      String reason = null;

      if (args.length != 1) {
        try {
          period = Integer.parseInt(args[1]);
          if (period <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
          bot.sendBack(
              update,
              SendMessage.builder().text("Укажите нормальный срок (целое положительное число)").build());
          return;
        }

        if (args.length > 2) {
          reason = Joiner.on(' ').join(StringUtils.skipOne(StringUtils.skipOne(args)));
        }
      }

      Optional<ChatMember> member =
          Main.getBot().getChatStorage().lookupMember(update.getMessage().getChatId().toString(), args[0]);
      if (!member.isPresent()) {
        bot.sendBack(
            update,
            SendMessage.builder()
                .text("Не могу найти юзера в сообщении. Проверьте ник.")
                .replyToMessageId(update.getMessage().getMessageId())
                .build());
        return;
      }

      Main.getBot()
          .getPunishmentStorage()
          .addPunishment(
              member.get().getUser().getId(),
              new MutePunishment(period, ChronoUnit.SECONDS, reason));

      bot.sendBack(
          update,
          SendMessage.builder()
              .chatId("")
              .text(
                  update.getMessage().getFrom().getUserName()
                      + " замутил @"
                      + member.get().getUser().getUserName()
                      + (reason != null ? " по причине '" + reason + "'" : "")
                      + " на "
                      + period
                      + "с.")
              .build());
    }
  }
}
