package me.theseems.tomshelby.defaultpack.handlers;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.storage.TomMeta;
import me.theseems.tomshelby.update.SimpleUpdateHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PollAnswerHandler extends SimpleUpdateHandler {
  public static class PollMessage {
    private final long chatId;
    private final int messageId;

    public PollMessage(long chatId, int messageId) {
      this.chatId = chatId;
      this.messageId = messageId;
    }
  }

  private Map<String, PollMessage> pollToChatMap;
  private File file;
  private static PollAnswerHandler instance;

  private PollAnswerHandler() {
    pollToChatMap = new HashMap<>();
  }

  private void save() {
    try {
      FileWriter writer = new FileWriter(file);
      new GsonBuilder().create().toJson(pollToChatMap, writer);
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static PollAnswerHandler getInstance() {
    return instance;
  }

  public static PollAnswerHandler loadFrom(File file) {
    if (instance != null) throw new IllegalStateException("Instance already exists");

    PollAnswerHandler handler = new PollAnswerHandler();
    handler.file = file;

    if (file.exists()) {
      try {
        handler.pollToChatMap =
            new GsonBuilder()
                .create()
                .fromJson(
                    new FileReader(file), new TypeToken<Map<String, PollMessage>>() {}.getType());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }

    if (handler.pollToChatMap == null) {
      handler.pollToChatMap = new HashMap<>();
    }

    return instance = handler;
  }

  public static void unload() {
    instance = null;
  }

  public void addPoll(String pollId, PollMessage chatId) {
    pollToChatMap.put(pollId, chatId);
    save();
  }

  public void removePoll(String pollId) {
    pollToChatMap.remove(pollId);
    save();
  }

  public boolean proxyHandleUpdate(ThomasBot bot, Update update) throws TelegramApiException {
    if (!update.hasPollAnswer()) return true;

    String pollId = update.getPollAnswer().getPollId();
    if (!pollToChatMap.containsKey(pollId)) return false;

    PollMessage pollMessage = pollToChatMap.get(pollId);
    User user = update.getPollAnswer().getUser();
    String userName;

    if (user.getUserName() != null) {
      userName = user.getUserName();
    } else {
      user.getFirstName();
      userName = user.getFirstName() + Optional.ofNullable(user.getLastName()).orElse("");
    }

    TomMeta meta = bot.getChatStorage().getChatMeta(String.valueOf(pollMessage.chatId));
    String positiveReaction = meta.getString("pollPositive").orElse("\uD83D\uDE18"); // '😘'
    String negativeReaction = meta.getString("pollNegative").orElse("\uD83D\uDE1E"); // '😞'
    String rudeReaction = meta.getString("pollRude").orElse("\uD83E\uDD2C"); // '🤬'

    String text = "";
    if (update.getPollAnswer().getOptionIds().contains(0)) {
      text += positiveReaction;
    } else if (update.getPollAnswer().getOptionIds().contains(1)) {
      text += negativeReaction;
    } else if (update.getPollAnswer().getOptionIds().contains(2)) {
      text += rudeReaction;
    }

    if (!text.isEmpty())
      bot.execute(
          SendMessage.builder()
              .text(text + " @" + userName)
              .chatId(String.valueOf(pollMessage.chatId))
              .replyToMessageId(pollMessage.messageId)
              .build());
    return false;
  }

  @Override
  public boolean handleUpdate(ThomasBot bot, Update update) {
    try {
      return proxyHandleUpdate(bot, update);
    } catch (TelegramApiException e) {
      e.printStackTrace();
      return false;
    }
  }
}
