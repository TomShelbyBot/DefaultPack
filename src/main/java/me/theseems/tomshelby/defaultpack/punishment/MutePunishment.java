package me.theseems.tomshelby.defaultpack.punishment;

import me.theseems.tomshelby.punishment.PunishmentType;
import me.theseems.tomshelby.punishment.TimedPunishment;

import java.time.temporal.ChronoUnit;

public class MutePunishment extends TimedPunishment {
  public MutePunishment(Integer period, ChronoUnit unit, String reason) {
    super(period, unit, reason);
  }

  @Override
  public PunishmentType getType() {
    return PunishmentType.MUTE;
  }
}
