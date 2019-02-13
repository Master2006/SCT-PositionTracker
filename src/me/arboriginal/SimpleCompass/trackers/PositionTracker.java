package me.arboriginal.SimpleCompass.trackers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import com.google.common.collect.ImmutableMap;
import me.arboriginal.SimpleCompass.plugin.AbstractTracker;
import me.arboriginal.SimpleCompass.plugin.SimpleCompass;

public class PositionTracker extends AbstractTracker {
  // ----------------------------------------------------------------------------------------------
  // Constructor methods
  // ----------------------------------------------------------------------------------------------

  public PositionTracker(SimpleCompass plugin) {
    super(plugin);
  }

  // ----------------------------------------------------------------------------------------------
  // Tracker methods
  // ----------------------------------------------------------------------------------------------

  @Override
  public String github() {
    return "arboriginal/SCT-PositionTracker";
  }

  @Override
  public String trackerID() {
    return "POSITION";
  }

  @Override
  public String version() {
    return "3";
  }

  // ----------------------------------------------------------------------------------------------
  // Actions methods
  // ----------------------------------------------------------------------------------------------

  @Override
  public List<TrackingActions> getActionsAvailable(Player player, boolean keepUnavailable) {
    List<TrackingActions> list = super.getActionsAvailable(player, keepUnavailable);

    if (player.hasPermission("scompass.track." + trackerID() + ".manage")) {
      list.add(TrackingActions.ADD);
      list.add(TrackingActions.DEL);
    }

    if (keepUnavailable || !availableTargets(player, "").isEmpty()) {
      list.add(TrackingActions.START);
      list.add(TrackingActions.STOP);
    }

    return list;
  }

  @Override
  public TargetSelector requireTarget(TrackingActions action) {
    return action.equals(TrackingActions.ADD) ? TargetSelector.NEWCOORDS : super.requireTarget(action);
  }

  // ----------------------------------------------------------------------------------------------
  // Targets methods
  // ----------------------------------------------------------------------------------------------

  @Override
  public List<String> availableTargets(Player player, String startWith) {
    List<String> list = new ArrayList<String>();
    if (datas().getKeys(false).isEmpty()) return list;

    datas().getKeys(false).forEach(candidate -> {
      if (startWith.isEmpty() || candidate.toLowerCase().startsWith(startWith.toLowerCase())) list.add(candidate);
    });

    return listFiltered(player, list);
  }

  @Override
  public List<String> list(Player player, TrackingActions action, String startWith) {
    return listFiltered(player, super.list(player, action, startWith));
  }

  // ----------------------------------------------------------------------------------------------
  // Command methods
  // ----------------------------------------------------------------------------------------------

  @Override
  public boolean perform(Player player, String command, TrackingActions action, String target, String[] args) {
    if (target == null && !action.equals(TrackingActions.ADD)) return false;

    switch (action) {
      case ADD:
        if (args.length < 3)
          sendMessage(player, "missing_target");
        else if (set(player, args[2], getCoords(player, args))) {
          sendMessage(player, "ADD", ImmutableMap.of("target", args[2]));
          if (settings.getBoolean("settings.auto_activated")) activate(player, args[2], false);
        }
        else
          sendMessage(player, "target_exists", ImmutableMap.of("target", args[2]));
        break;

      case DEL:
        sendMessage(player, del(player, args[2]) ? "DEL" : "target_not_found", ImmutableMap.of("target", args[2]));
        break;

      case START:
        if (!activate(player, args[2], true)) break;
        sendMessage(player, "START", ImmutableMap.of("target", args[2]));
        break;

      case STOP:
        disable(player, args[2]);
        sendMessage(player, "STOP", ImmutableMap.of("target", args[2]));
        break;

      default:
        return false;
    }

    return true;
  }

  // ----------------------------------------------------------------------------------------------
  // Targets methods
  // ----------------------------------------------------------------------------------------------

  @Override
  public boolean set(Player player, String name, double[] coords) {
    String key = key(player, name);

    if (datas().contains(key)) return false;
    if (limitReached(player, TrackingActions.ADD, true, datas().getKeys(false).size())) return false;

    boolean success = (save(key + ".x", coords[0]) && save(key + ".z", coords[1]));
    if (success) return true;

    save(key, null);
    return false;
  }

  // ----------------------------------------------------------------------------------------------
  // Storage methods
  // ----------------------------------------------------------------------------------------------

  @Override
  public MemorySection datas() {
    return (MemorySection) settings.getConfigurationSection("positions");
  }

  @Override
  public String key(Player player, String name) {
    return (name == null ? "" : name.toLowerCase());
  }

  @Override
  public boolean save(String key, Object value) {
    if (key.isEmpty()) return false; // @formatter:off
    datas().set(key, value);
    try { settings.save(sf); } catch (IOException e) { return false; }
    return true; // @formatter:on
  }
}
