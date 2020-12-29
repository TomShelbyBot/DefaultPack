package me.theseems.tomshelby.defaultpack.commands;

import com.google.common.base.Joiner;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.AdminPermissibleBotCommand;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import me.theseems.tomshelby.defaultpack.handlers.PollAnswerHandler;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;

public class GoBotCommand extends SimpleBotCommand implements AdminPermissibleBotCommand {
  public GoBotCommand() {
    super(SimpleCommandMeta.onLabel("go").description("Отправит опрос со слов 'Погнали ...'."));
  }

  @Override
  public void handle(ThomasBot bot, String[] args, Update update) {
    try {
      SendPoll sendPoll =
          new SendPoll()
              .setChatId(update.getMessage().getChatId())
              .setQuestion("Погнали " + Joiner.on(' ').join(args))
              .setAnonymous(false)
              .setOptions(Arrays.asList("Да", "Нет", "\uD83E\uDD2C\uD83E\uDD2C\uD83E\uDD2C"));

      Message message = bot.execute(sendPoll);
      int pollMessageId = message.getMessageId();

      bot.execute(
          new PinChatMessage()
              .setChatId(update.getMessage().getChatId())
              .setMessageId(pollMessageId));

      // Add poll to the container so that we'll be able
      // to send a reaction to the corresponding chat
      PollAnswerHandler.getInstance()
          .addPoll(
              message.getPoll().getId(),
              new PollAnswerHandler.PollMessage(message.getChatId(), message.getMessageId()));
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
}
