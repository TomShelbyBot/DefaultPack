package me.theseems.tomshelby.defaultpack.commands;

import com.google.common.base.Joiner;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.AdminPermissibleBotCommand;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collection;
import java.util.stream.Collectors;

public class AllBotCommand extends SimpleBotCommand implements AdminPermissibleBotCommand {
  public AllBotCommand() {
    super(SimpleCommandMeta.onLabel("all").description("Упомянуть всех, кого бот знает."));
  }

  @Override
  public void handle(ThomasBot bot, String[] args, Update update) {
    String message = Joiner.on(' ').join(args);
    if (message.isEmpty()) {
      bot.replyBackText(update, "Укажите сообщение!");
      return;
    }

    message = message.replaceAll("!", "\\\\!");
    Collection<ChatMember> resolvableNicknames =
        bot.getChatStorage().getResolvableUsernames(update.getMessage().getChatId().toString()).stream()
            .map(s ->
                    bot.getChatStorage()
                        .lookupMember(update.getMessage().getChatId().toString(), s)
                        .orElse(null))
            .collect(Collectors.toList());

    StringBuilder result = new StringBuilder().append(message);
    for (ChatMember resolvableNickname : resolvableNicknames) {
      // That certainly needs to be explained.
      // We have markup support for telegram messages like so: [<title>](<url>)
      // For title we use Alt+0173 symbol ('­') and for url is a tg link to user (id)
      // That's how we get the invisible (empty) mention
      result.append("[­](tg://user?id=").append(resolvableNickname.getUser().getId()).append(")");
    }

    try {
      SendMessage sendMessage =
              SendMessage.builder()
                      .chatId(update.getMessage().getChatId().toString())
                      .text("\uD83D\uDECE " + result.toString())
                      .build();

      sendMessage.enableMarkdownV2(true);
      bot.execute(sendMessage); // '🛎'
    } catch (TelegramApiException e) {
      bot.replyBackText(update, "Не могу отправить такое сообщение, аккуратнее с / и !");
    }
  }
}
