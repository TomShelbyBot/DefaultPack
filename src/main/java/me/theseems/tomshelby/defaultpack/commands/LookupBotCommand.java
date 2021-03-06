package me.theseems.tomshelby.defaultpack.commands;

import com.google.common.base.Joiner;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
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
    Optional<Integer> userId = bot.getChatStorage().lookup(chatId, args[0]);

    if (!userId.isPresent()) {
      bot.replyBack(update, new SendMessage().setText("А вот этого гражданина я не знаю."));
      String knownNicknames =
          Joiner.on(' ')
              .join(bot.getChatStorage().getResolvableUsernames(chatId))
              .replaceFirst("@", " ");

      bot.sendBack(
          update,
          new SendMessage()
              .setText(
                  "Мне известны ("
                      + bot.getChatStorage().getResolvableUsernames(chatId).size()
                      + "): \n"
                      + knownNicknames));
      return;
    }

    try {
      ChatMember member =
          bot.execute(new GetChatMember().setUserId(userId.get()).setChatId(chatId));

      String firstName =
          Optional.ofNullable(member.getUser().getFirstName()).orElse("<Имя не указано>");
      String lastName =
          Optional.ofNullable(member.getUser().getLastName()).orElse("<Фамилия не указана>");
      String title = Optional.ofNullable(member.getCustomTitle()).orElse("<Приписки нет>");

      bot.sendBack(
          update,
          new SendMessage()
              .setText(
                  "Гражданин "
                      + firstName
                      + " "
                      + lastName
                      + " "
                      + member.getUser().getUserName()
                      + " с припиской "
                      + title));

    } catch (TelegramApiException e) {
      bot.sendBack(
          update,
          new SendMessage().setText("Произошла ошибочка. Кажется, я не узнаю этого пользователя."));
      e.printStackTrace();
    }
  }
}
