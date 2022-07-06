package me.theseems.tomshelby.defaultpack.handlers;


import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.update.SimpleUpdateHandler;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class NonStickerModeHandler extends SimpleUpdateHandler {
  /**
   * Handle update
   *
   * @param bot to handle for
   * @param update to handle
   * @return whether we should process update next or not
   */
  @Override
  public boolean handleUpdate(ThomasBot bot, Update update) {
    if (bot.getChatStorage()
            .getChatMeta(update.getMessage().getChatId().toString())
            .getBoolean("stickerMode")
            .orElse(false)
        && update.getMessage().hasSticker()) {
      try {
        bot.execute(
            DeleteMessage.builder()
                .messageId(update.getMessage().getMessageId())
                .chatId(update.getMessage().getChatId().toString())
                .build());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
      return false;
    }
    return true;
  }
}
