package me.theseems.tomshelby.defaultpack.commands;

import com.google.common.base.Joiner;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.AdminPermissibleBotCommand;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import me.theseems.tomshelby.defaultpack.handlers.PollAnswerHandler;
import me.theseems.tomshelby.storage.TomMeta;
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
      TomMeta chatMeta = bot.getChatStorage().getChatMeta(update.getMessage().getChatId().toString());
      String prefix = chatMeta.getString("goPrefix").orElse("Погнали ");
      String positiveOption = chatMeta.getString("goPollPositive").orElse("Да");
      String negativeOption = chatMeta.getString("goPollNegative").orElse("Нет");
      String rudeOption = chatMeta.getString("goPollRude").orElse("\uD83E\uDD2C"); // '🤬'

      SendPoll sendPoll =
          SendPoll.builder()
              .chatId(update.getMessage().getChatId().toString())
              .question(prefix + Joiner.on(' ').join(args))
              .isAnonymous(false)
              .options(Arrays.asList(positiveOption, negativeOption, rudeOption))
              .build();

      Message message = bot.execute(sendPoll);
      int pollMessageId = message.getMessageId();

      bot.execute(
          PinChatMessage.builder()
              .chatId(update.getMessage().getChatId().toString())
              .messageId(pollMessageId)
              .build());

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
