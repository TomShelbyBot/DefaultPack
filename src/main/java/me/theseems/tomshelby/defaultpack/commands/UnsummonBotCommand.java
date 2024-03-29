package me.theseems.tomshelby.defaultpack.commands;

import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

public class UnsummonBotCommand extends SimpleBotCommand {

  public UnsummonBotCommand() {
    super(
        new SimpleCommandMeta()
            .label("unsummon")
            .description("Сбросить призыв прописать комманду"));
  }

  @Override
  public void handle(ThomasBot bot, String[] args, Update update) {
    bot.sendBack(update,
            SendMessage.builder()
                    .chatId("")
                    .text("Сброшено")
                    .replyMarkup(ReplyKeyboardRemove.builder().removeKeyboard(true).build())
                    .build());
  }
}
