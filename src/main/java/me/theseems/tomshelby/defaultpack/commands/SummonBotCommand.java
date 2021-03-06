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
      bot.sendBack(update, new SendMessage().setText("Укажите команду которую нужно прописать!"));
      return;
    }

    String action = Joiner.on(' ').join(args);
    CommandUtils.CommandSkeleton skeleton = CommandUtils.extractCommand(action);
    if (!skeleton.success) {
      bot.sendBack(update, new SendMessage().setText("Не удалось распознать команду."));
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
          new SendMessage()
              .setText("Эту команду запрещено призывать в связи с техническими ограничениями"));
      return;
    }

    KeyboardRow row = new KeyboardRow();
    row.add(action);

    TomMeta meta = bot.getChatStorage().getChatMeta(update.getMessage().getChatId());
    int summonActiveMils = meta.getInteger(SUMMON_ACTIVE_MILS_KEY).orElse(5000);

    bot.sendBack(
        update,
        new SendMessage()
            .setReplyMarkup(new ReplyKeyboardMarkup().setKeyboard(Collections.singletonList(row)))
            .setText(
                "Опаааа а тут просят вас это самое.. ну прописать комманду ("
                    + summonActiveMils / 1000
                    + " сек на реакцию) "
                    + action));

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
