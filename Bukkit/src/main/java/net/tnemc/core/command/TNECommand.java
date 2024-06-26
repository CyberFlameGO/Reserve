package net.tnemc.core.command;

import net.tnemc.core.Reserve;
import net.tnemc.core.utils.Message;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by creatorfromhell on 8/9/2017.
 * <p>
 * Reserve API
 * <p>
 * Copyright (C) 2021 creatorfromhell
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA, or see
 * it at <https://www.gnu.org/licenses/lgpl-3.0.txt>.
 **/
public abstract class TNECommand {

  protected final Reserve plugin;

  public TNECommand(Reserve plugin) {
    this.plugin = plugin;
  }

  public final List<TNECommand> subCommands = new ArrayList<>();

  public abstract String getName();

  public abstract String[] getAliases();

  public abstract String getNode();

  public abstract boolean console();

  public String getHelp() {
    return "Command help coming soon!";
  }

  public String[] getHelpLines() {
    Message message = new Message(getHelp());
    return message.grabWithNew(null);
  }

  public void help(CommandSender sender) {
    help(sender, 1);
  }

  public void help(CommandSender sender, Integer page) {
    List<String[]> help = new ArrayList<>();
    if(subCommands.size() > 0) {
      for(TNECommand sub : subCommands) {
        if(sender.hasPermission(sub.getNode())) {
          help.add(sub.getHelpLines());
        }
      }
    } else {
      if(sender.hasPermission(getNode())) {
        help.add(getHelpLines());
      }
    }


    int linesPerPage = 5;
    int remaining = linesPerPage;
    Integer maxPage = 1;
    for(int i = 0; i < help.size(); i++) {
      if(remaining <= 0) {
        maxPage++;
        remaining = linesPerPage;
      }
      int length = help.get(i).length;
      if(i == help.size() - 1 && remaining - length < 0) { maxPage++; }
      remaining -= length;
    }

    Integer loopPage = 1;
    remaining = linesPerPage;
    Integer helpPage = (page > maxPage)? maxPage : page;
    List<Integer> send = new ArrayList<>();
    for(int i = 0; i < help.size(); i++) {
      if(remaining <= 0) {
        loopPage++;
        remaining = linesPerPage;
      }
      int length = help.get(i).length;
      if(i == help.size() - 1 && remaining - length < 0) { loopPage++; }
      if(loopPage.equals(helpPage)) { send.add(i); }
      remaining -= length;
    }

    if(subCommands.size() > 0) {
      String name = getName();
      String formatted = name.substring(0, 1).toUpperCase() + name.substring(1);
      sender.sendMessage(ChatColor.GOLD + "~~~" + ChatColor.WHITE + formatted + " Help " + helpPage + "/" + maxPage + ChatColor.GOLD + "~~~");
    }

    for(Integer i : send) {
      for(String s : help.get(i)) {
        String message = (s.contains("Messages."))? new Message(s).grab("", sender) : s;
        message = message.replaceFirst("/", "<green>/").replaceFirst("-", "<white>-");
        new Message(message).translate("", sender);
      }
    }
  }

  public Boolean locked() {
    return false;
  }

  public Boolean confirm() {
    return false;
  }

  public Boolean activated(String world, String player) {
    return true;
  }

  public boolean execute(CommandSender sender, String command, String[] arguments) {

    if(arguments.length == 0) {
      help(sender);
      return false;
    }

    TNECommand sub = FindSub(arguments[0]);
    if(sub == null && !arguments[0].equalsIgnoreCase("help") && !arguments[0].equalsIgnoreCase("?")) {
      Message noCommand = new Message("Messages.Command.None");
      noCommand.addVariable("$commands", "/" + getName());
      noCommand.addVariable("$arguments", arguments[0]);
      noCommand.translate(sender);
      return false;
    }

    if(arguments[0].equalsIgnoreCase("help") || arguments[0].equalsIgnoreCase("?")) {
      Integer page = (arguments.length >= 2)? getPage(arguments[1]) : 1;
      help(sender, page);
      return false;
    }

    if(sub.canExecute(sender) && arguments.length >= 2 && arguments[1].equalsIgnoreCase("?") || sub.canExecute(sender) && arguments.length >= 2 && arguments[1].equalsIgnoreCase("help")) {
      int page = (arguments.length >= 3)? getPage(arguments[2]) : 1;

      sub.help(sender, page);
      return false;
    }

    if(!sub.canExecute(sender)) {
      Message unable = new Message("Messages.Command.Unable");
      unable.addVariable("$commands", "/" + getName());
      unable.translate(sender);
      return false;
    }
    return sub.execute(sender, command, removeSub(arguments));
  }

  protected String[] removeSub(String[] oldArguments) {
    String[] arguments = new String[oldArguments.length - 1];
    System.arraycopy(oldArguments, 1, arguments, 0, oldArguments.length - 1);
    return arguments;
  }

  public TNECommand FindSub(String name) {
    for(TNECommand sub : subCommands) {
      if(sub.getName().equalsIgnoreCase(name)) {
        return sub;
      }
    }
    for(TNECommand sub : subCommands) {
      for(String s : sub.getAliases()) {
        if(s.equalsIgnoreCase(name)) {
          return sub;
        }
      }
    }
    return null;
  }

  public Integer getPage(String pageValue) {
    int page = 1;
    try {
      page = Integer.parseInt(pageValue);
    } catch(Exception ignored) {

    }
    return page;
  }

  public boolean canExecute(CommandSender sender) {
    if(sender instanceof Player) {
      return sender.hasPermission(getNode());
    }
    return console();
  }

  protected Map<String, String> getArguments(String[] arguments) {
    Map<String, String> parsed = new HashMap<>();
    for(int i = 0; i < arguments.length; i++) {
      if(arguments[i].contains(":")) {
        String[] broken = arguments[i].split(":");
        parsed.put(broken[0], broken[1]);
        continue;
      }
      parsed.put(i + "", arguments[i]);
    }
    return parsed;
  }

  protected Player getPlayer(CommandSender sender) {
    if(sender instanceof Player) {
      return (Player)sender;
    }
    return null;
  }

  @SuppressWarnings("deprecation")
  protected Player getPlayer(CommandSender sender, String username) {
    if(username != null) {
      List<Player> matches = sender.getServer().matchPlayer(username);
      if(!matches.isEmpty()) {
        return matches.get(0);
      }
      sender.sendMessage(ChatColor.WHITE + "Player \"" + ChatColor.RED + username + ChatColor.WHITE + "\" could not be found!");
      return null;
    } else {
      if(sender instanceof Player) {
        return (Player)sender;
      }
    }
    return null;
  }
}
