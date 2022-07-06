package me.theseems.tomshelby.defaultpack.commands;

import com.google.common.base.Joiner;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

public class LookupBotCommand extends SimpleBotCommand {
  public LookupBotCommand() {
    super(
        SimpleCommandMeta.onLabel("check")
            .aliases("lookup")
            .description("Основная информация из сведений семьи Шелби."));
  }

  @Override
  public void handle(ThomasBot bot, String[] args, Update update) {
    if (args.length == 0) {
      bot.replyBackText(update, "Так.. ник-то укажи!");
      return;
    }

    // Remove leading @ so not to disturb users
    if (args[0].startsWith("@")) {
      args[0] = args[0].substring(1);
    }

    Long chatId = update.getMessage().getChatId();
    Optional<Long> userId = bot.getChatStorage().lookup(chatId.toString(), args[0]);

    if (!userId.isPresent()) {
      bot.replyBack(update, SendMessage.builder()
              .chatId("").text("А вот этого гражданина я не знаю.").build());
      String knownNicknames =
          Joiner.on(' ')
              .join(bot.getChatStorage().getResolvableUsernames(chatId.toString()))
              .replaceFirst("@", " ");

      bot.sendBack(
          update,
          SendMessage.builder()
              .chatId("")
              .text(
                  "Мне известны ("
                      + bot.getChatStorage().getResolvableUsernames(chatId.toString()).size()
                      + "): \n"
                      + knownNicknames)
                  .build());
      return;
    }

    try {
      ChatMember member =
          bot.execute(GetChatMember.builder().userId(userId.get()).chatId(chatId.toString()).build());

      String firstName =
          Optional.of(member.getUser().getFirstName()).orElse("<Имя не указано>");
      String lastName =
          Optional.ofNullable(member.getUser().getLastName()).orElse("<Фамилия не указана>");

      String title = "<Приписки нет>";
      if (member instanceof ChatMemberAdministrator) {
        title = ((ChatMemberAdministrator) member).getCustomTitle();
      }

      bot.sendBack(
          update,
          SendMessage.builder()
              .text(
                  "Гражданин "
                      + firstName
                      + " "
                      + lastName
                      + " "
                      + member.getUser().getUserName()
                      + " с припиской "
                      + title)
                  .build());

    } catch (TelegramApiException e) {
      bot.sendBack(
          update,
          SendMessage.builder().text("Произошла ошибочка. Кажется, я не узнаю этого пользователя.").build());
      e.printStackTrace();
    }
  }
}
