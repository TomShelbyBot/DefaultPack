package me.theseems.tomshelby.defaultpack.commands;

import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import me.theseems.tomshelby.util.CommandUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Random;

public class RandomNumberBotCommand extends SimpleBotCommand {
  public RandomNumberBotCommand() {
    super(
        new SimpleCommandMeta()
            .label("random")
            .aliases("number")
            .description("Получить рандомное число (диапазон тоже можно указать)."));
  }

  @Override
  public void handle(ThomasBot bot, String[] args, Update update) {
    int lowerBound = 1;
    int upperBound = 100;

    if (args.length == 1) {
      upperBound =
          CommandUtils.requirePositiveInt(
              args[0], "Укажите положительное число в качестве верхней границы для рандома!");
    } else if (args.length == 2) {
      lowerBound =
          CommandUtils.requirePositiveInt(
              args[0], "Укажите положительное число в качестве нижней границы для рандома!");
      upperBound =
          CommandUtils.requirePositiveInt(
              args[1], "Укажите положительное число в качестве верхней границы для рандома!");

      if (lowerBound > upperBound) {
        int temp = lowerBound;
        lowerBound = upperBound;
        upperBound = temp;
      }
    }

    if (upperBound > 10000) upperBound = 10000;
    int result = new Random().nextInt(upperBound - lowerBound + 1) + lowerBound;

    SendMessage sendMessage =
            SendMessage.builder()
                    .chatId("")
                    .replyToMessageId(update.getMessage().getMessageId())
                    .text(
                            "\n \nВыбираем число от "
                                    + lowerBound
                                    + " до "
                                    + upperBound
                                    + "\nВам выпало: *"
                                    + result
                                    + "*")
                    .build();

    sendMessage.enableMarkdown(true);
    bot.sendBack(update, sendMessage);
  }
}
