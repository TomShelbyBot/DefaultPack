package me.theseems.tomshelby.defaultpack.commands;

import com.google.common.base.Joiner;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.AdminPermissibleBotCommand;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;

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

    Collection<ChatMember> resolvableNicknames =
        bot.getChatStorage().getResolvableUsernames(update.getMessage().getChatId()).stream()
            .map(
                s ->
                    bot.getChatStorage()
                        .lookupMember(update.getMessage().getChatId(), s)
                        .orElse(null))
            .collect(Collectors.toList());

    StringBuilder result = new StringBuilder().append(message);
    for (ChatMember resolvableNickname : resolvableNicknames) {
      result.append("[­](tg://user?id=").append(resolvableNickname.getUser().getId()).append(")");
    }

    bot.replyBack(update, new SendMessage().setText(result.toString()).enableMarkdownV2(true));
  }
}
