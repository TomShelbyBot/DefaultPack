package me.theseems.tomshelby.defaultpack;

import me.theseems.tomshelby.command.BotCommand;
import me.theseems.tomshelby.defaultpack.commands.*;
import me.theseems.tomshelby.defaultpack.handlers.NonStickerModeHandler;
import me.theseems.tomshelby.defaultpack.handlers.PollAnswerHandler;
import me.theseems.tomshelby.defaultpack.handlers.WelcomeHandler;
import me.theseems.tomshelby.defaultpack.punishment.DeleteMessageProcessor;
import me.theseems.tomshelby.defaultpack.punishment.MumbleMessageProcessor;
import me.theseems.tomshelby.pack.JavaBotPackage;
import me.theseems.tomshelby.punishment.PunishmentType;
import me.theseems.tomshelby.update.SimpleUpdateHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends JavaBotPackage {
  private static final List<BotCommand> botCommandList = Arrays.asList(
      new GooseBotCommand(),
      new WallBotCommand(),
      new BeatBotCommand(),
      new LookupBotCommand(),
      new MuteBotCommand(),
      new UnmuteBotCommand(),
      new AllBotCommand(),
      new NoStickerBotCommand(),
      new GoBotCommand(),
      new ClapMuteBotCommand(),
      new CheckPunishmentsBotCommand(),
      new ThrowCoinBotCommand(),
      new RandomNumberBotCommand(),
      new SummonBotCommand(),
      new UnsummonBotCommand(),
      new SayBotCommand(),
      new RespectBotCommand(),
      new ToxicBotCommand(),
      new BombBotCommand()
  );

  private static List<SimpleUpdateHandler> updateHandlers;

  @Override
  public void onLoad() {
    System.out.println("Loading default pack");

    // Create plugin folder
    File directory = getPackageFolder();
    if (!directory.exists()) {
      directory.mkdir();
    }

    // Create polls file
    File pollFile = new File(directory, "polls.json");
    if (!pollFile.exists()) {
      try {
        pollFile.createNewFile();
      } catch (IOException e) {
        System.err.println("Error creating polls directory");
        e.printStackTrace();
      }
    }

    updateHandlers = new ArrayList<>();
  }

  @Override
  public void onEnable() {
    // Enable commands
    for (BotCommand botCommand : botCommandList) {
      getBot().getCommandContainer().attach(botCommand);
    }

    // Enable handlers
    getBot().getPunishmentHandler().add(new DeleteMessageProcessor());
    getBot().getPunishmentHandler().add(new MumbleMessageProcessor());

    if (PollAnswerHandler.getInstance() == null)
      PollAnswerHandler.loadFrom(new File(getPackageFolder(), "polls.json"));

    updateHandlers = new ArrayList<>();
    updateHandlers.add(PollAnswerHandler.getInstance());
    updateHandlers.add(new WelcomeHandler());
    updateHandlers.add(new NonStickerModeHandler());

    for (int i = 0; i < updateHandlers.size(); i++) {
      updateHandlers.get(i).setPriority(100 + i);
      getBot().getUpdateHandlerManager().addUpdateHandler(updateHandlers.get(i));
    }
  }

  @Override
  public void onDisable() {
    System.out.println("Disabling default pack");

    for (BotCommand botCommand : botCommandList) {
      getBot().getCommandContainer().detach(botCommand.getMeta().getLabel());
    }

    getBot().getPunishmentHandler().remove(PunishmentType.MUTE, "DeleteMessage");
    getBot().getPunishmentHandler().remove(PunishmentType.CLAP_MUTE, "MumbleMessage");

    PollAnswerHandler.unload();
    for (SimpleUpdateHandler updateHandler : updateHandlers) {
      getBot().getUpdateHandlerManager().removeUpdateHandler(updateHandler);
    }

    updateHandlers.clear();
  }
}
