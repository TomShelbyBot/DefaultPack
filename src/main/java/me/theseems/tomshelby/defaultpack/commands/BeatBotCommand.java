package me.theseems.tomshelby.defaultpack.commands;

import com.google.common.base.Joiner;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BeatBotCommand extends SimpleBotCommand {
  public BeatBotCommand() {
    super(SimpleCommandMeta.onLabel("beat").description("Изрезать..."));
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
          update,
          SendMessage.builder()
              .text("Покажи кого изрезать.")
              .replyToMessageId(update.getMessage().getMessageId())
              .build());
    } else {
      SendMessage sendMessage = SendMessage.builder()
              .text("__ "
                      + "@"
                      + (update.hasMessage()
                      ? update.getMessage().getFrom().getUserName()
                      : update.getCallbackQuery().getFrom().getUserName())
                      + " всячески изуродовал козырьком "
                      + args[0]
                      + ". "
                      + Joiner.on(' ')
                      .join(Arrays.stream(args).skip(1).collect(Collectors.toList()))
                      + " __")
              .chatId("")
              .build();
      sendMessage.enableMarkdown(true);

      bot.sendBack(update, sendMessage);
    }
  }
}
