package me.theseems.tomshelby.defaultpack.commands;

import me.theseems.tomshelby.Main;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.AdminPermissibleBotCommand;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import me.theseems.tomshelby.punishment.Punishment;
import me.theseems.tomshelby.punishment.PunishmentType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public class UnmuteBotCommand extends SimpleBotCommand implements AdminPermissibleBotCommand {
  public UnmuteBotCommand() {
    super(
        SimpleCommandMeta.onLabel("unmute")
            .aliases("pardon")
            .description("Размутить. Выдать.. Право.. Голоса"));
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
      bot.sendBack(update, SendMessage.builder().text("Укажите юзера кому нужно снять глушилку!").build());
    } else {

      if (args[0].startsWith("@")) args[0] = args[0].substring(1);

      Optional<ChatMember> member =
          Main.getBot().getChatStorage().lookupMember(update.getMessage().getChatId().toString(), args[0]);
      if (!member.isPresent()) {
        bot.sendBack(update, SendMessage.builder().text("Не могу найти гражданина в сообщении.").build());
        return;
      }

      ChatMember actual = member.get();
      Optional<Punishment> punishmentOptional =
          bot.getPunishmentStorage()
              .getAnyActivePunishment(
                  actual.getUser().getId(), PunishmentType.MUTE, PunishmentType.CLAP_MUTE);

      if (!punishmentOptional.isPresent()) {
        bot.sendBack(
            update,
            SendMessage.builder()
                .text("У " + actual.getUser().getUserName() + " нет всунутых затычек")
                .replyToMessageId(update.getMessage().getMessageId())
                .build());
        return;
      }

      Punishment punishment = punishmentOptional.get();
      bot.getPunishmentStorage().removePunishment(actual.getUser().getId(), punishment);

      String reason =
          (punishment.getReason().isPresent()
              ? "'" + punishment.getReason().get() + "'"
              : "_<НЕ УКАЗАНА>_");

      SendMessage sendMessage =
              SendMessage.builder()
                      .chatId("")
                      .text(
                              update.getMessage().getFrom().getUserName()
                                      + " размутил @"
                                      + actual.getUser().getUserName()
                                      + "\nЗаглушка была с надписью "
                                      + reason)
                      .replyToMessageId(update.getMessage().getMessageId())
                      .build();

      sendMessage.enableMarkdown(true);
      bot.sendBack(update, sendMessage);
    }
  }
}
