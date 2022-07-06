package me.theseems.tomshelby.defaultpack.commands;

import com.google.common.base.Joiner;
import me.theseems.tomshelby.ThomasBot;
import me.theseems.tomshelby.command.SimpleBotCommand;
import me.theseems.tomshelby.command.SimpleCommandMeta;
import me.theseems.tomshelby.storage.TomMeta;
import me.theseems.tomshelby.util.CommandUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

public class SummonBotCommand extends SimpleBotCommand {
  public static final String SUMMON_ACTIVE_MILS_KEY = "summonActiveMils";
  public static final List<String> SUMMON_BLACKLIST = Arrays.asList("bomb", "summon");

  public SummonBotCommand() {
    super(
        new SimpleCommandMeta().label("summon").description("Отправить призыв прописать комманду"));
  }

  @Override
  public void handle(ThomasBot bot, String[] args, Update update) {
    if (args.length == 0) {
      bot.sendBack(update, SendMessage.builder().text("Укажите команду которую нужно прописать!").build());
      return;
    }

    String action = Joiner.on(' ').join(args);
    CommandUtils.CommandSkeleton skeleton = CommandUtils.extractCommand(action);
    if (!skeleton.success) {
      bot.sendBack(update, SendMessage.builder().text("Не удалось распознать команду.").build());
      return;
    }

    String mainLabel =
        bot.getCommandContainer()
            .get(skeleton.label)
            .flatMap(botCommand -> Optional.ofNullable(botCommand.getMeta().getLabel()))
            .orElse("");
    if (SUMMON_BLACKLIST.contains(mainLabel)) {
      bot.sendBack(
          update,
          SendMessage.builder()
              .text("Эту команду запрещено призывать в связи с техническими ограничениями")
              .build());
      return;
    }

    KeyboardRow row = new KeyboardRow();
    row.add(action);

    TomMeta meta = bot.getChatStorage().getChatMeta(update.getMessage().getChatId().toString());
    int summonActiveMils = meta.getInteger(SUMMON_ACTIVE_MILS_KEY).orElse(5000);

    bot.sendBack(
        update,
        SendMessage.builder()
            .chatId("")
            .replyMarkup(ReplyKeyboardMarkup.builder().keyboard(Collections.singletonList(row)).build())
            .text("Опаааа а тут просят вас это самое.. ну прописать комманду ("
                    + summonActiveMils / 1000
                    + " сек на реакцию) "
                    + action)
            .build());

    Timer timer = new Timer();
    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            bot.getCommandContainer()
                .get("unsummon")
                .ifPresent(command -> command.handle(bot, new String[] {}, update));
          }
        },
        summonActiveMils);
  }
}
