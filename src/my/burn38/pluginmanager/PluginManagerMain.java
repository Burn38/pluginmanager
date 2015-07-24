package my.burn38.pluginmanager;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginManagerMain extends JavaPlugin implements Listener {
	  Logger log = Logger.getLogger("Minecraft");
	  PluginDescriptionFile pdfFile = getDescription();
	  String pluginTag = "";
	  //PluginManagerAPI api;
	  Plugin plugin;
	  
	  public void onEnable() {
		  plugin = this;
	    loadConfiguration();
	    PluginDescriptionFile pdfFile = getDescription();
	    Bukkit.getServer().getPluginManager().registerEvents(this, this);
	    
	    this.log.info("[" + pdfFile.getName() + "]"+ " Plugin started on version " + pdfFile.getVersion() + " !");
	    
	    
	  }
	  public void onDisable() {
		  plugin = null;
	    PluginDescriptionFile pdfFile = getDescription();
	    this.log.info("[" + pdfFile.getName() + "] Plugin stopped !");
	  }
	  
		public void loadConfiguration() {
	 		getConfig().addDefault("plugin.tag", "[Plugin Manager]");
			
			//MESSAGES
				//ERRORS
					//PERMISSIONS
						getConfig().addDefault("messages.errors.permissions.plugin", " Tu n'as pas la permission d'utiliser ce plugin !");
						
					//GENERAL COMMAND ERRORS
						getConfig().addDefault("messages.errors.commands.args", " Erreur dans la commande. Impossible de continuer.");
						
					//ENABLE DISABLE RELOAD
						getConfig().addDefault("messages.errors.plugin.doesntExist", " Ce plugin n'existe pas !");
						getConfig().addDefault("messages.errors.enable.alreadyEnabled", " Ce plugin est déjà activé !");
						getConfig().addDefault("messages.errors.disable.alreadyDisabled", " Ce plugin est déjà désactivé !");
						getConfig().addDefault("messages.errors.unexpected", " Une erreur inconnue s'est produite, vérifiez les logs pour plus d'infos !");
						getConfig().addDefault("messages.errors.try.self", " Tu ne peux ni me désactiver, et encore moins m'activer (vu que je suis toujours actif) avec cette commande !");
						
				//INFOS
					//PLUGIN
						getConfig().addDefault("messages.infos.plugin.reload", "  Le plugin et sa configuration ont été rechargés");
					//COMMANDS
						getConfig().addDefault("messages.infos.enable.success", " Vous avez activé le plugin [%plugin%]");
						getConfig().addDefault("messages.infos.disable.success", " Vous avez désactivé le plugin [%plugin%]");
						getConfig().addDefault("messages.infos.reload.success", " Vous avez rechargé le plugin [%plugin%] en mode %reload_mode%");
						getConfig().addDefault("messages.infos.load.success", " Vous avez chargé le plugin [%plugin%]");
						getConfig().addDefault("messages.infos.unload.success", " Vous avez déchargé le plugin [%plugin%]");

			getConfig().options().copyDefaults(true);
			saveConfig();

			if (getConfig().getString("plugin.tag") != null) {
				pluginTag = ChatColor.translateAlternateColorCodes('&', getConfig().getString("plugin.tag"));
				if (pluginTag.contains("&")) pluginTag = ChatColor.translateAlternateColorCodes('&', pluginTag)+ChatColor.RESET;
			}
		}
		public void reloadConfiguration() {
			reloadConfig();
		}
		public boolean onCommand(CommandSender cs, Command command, String commandLabel, String[] args) {
		    if (commandLabel.equalsIgnoreCase("plm")) {
		    	if (cs instanceof Player) {
		    	 Player p = (Player)cs;
		    	 if (p.hasPermission("pluginmanager.use")) {
			      if (args.length == 1) {
			       String cmd = args[0];
			        if (cmd.equalsIgnoreCase("help")) {
			          help(p);
			          return true;
			        } else if (cmd.equalsIgnoreCase("reload")){
			        	reload(p, null);
			        	 return true;
			        } else {
			        	send("errors.commands.args", cs, null);
		        		return true;
			        }
			      } else if (args.length == 2) {
				    String cmd = args[0]; String arg = args[1];
			    	  if (cmd.equalsIgnoreCase("enable")) {
				          enable(p, arg);
				          return true;
				      } else if (cmd.equalsIgnoreCase("load")) {
				          load(p, arg);
				          return true;
				      } else if (cmd.equalsIgnoreCase("unload")) {
				          unload(p, arg);
				          return true;
				      } else if (cmd.equalsIgnoreCase("disable")) {
				          disable(p, arg);
				          return true;
				      } else {
				    	  send("errors.commands.args", cs, null);
				    	  return true;
				      }
			      } else if (args.length == 3) {
				    String cmd = args[0]; String type = args[1]; String arg = args[2];
			    	  if (cmd.equalsIgnoreCase("reload")) {
			    		  if (type.equalsIgnoreCase("hard")) {
			    			  reloadPl(cs, arg);
			    		  } else if (type.equalsIgnoreCase("soft")) {
			    			  reload(cs, arg);
			    		  } else {
			    			  send("errors.commands.args", cs, null);
					    	  return true;
			    		  }
				          return true;
				      } else {
				    	  send("errors.commands.args", cs, null);
				    	  return true;
				      }
			      } else {
			    	  return false;
			      }
			    } else {
			    	send("errors.permissions.plugin", cs, null);
			    	  return true;
			    }
		    } else {
		    	send(" La console et les commands blocks ne peuvent pas utiliser les commandes de ["+pdfFile.getName()+"] !", cs, null);
		    	return true;
		    }
	 }
			return false;
}
	  
	  
	  boolean enable(CommandSender cs, String s) {
			 PluginManager pm = Bukkit.getPluginManager();
			 Plugin pl = pm.getPlugin(s);
				 if (!(pl == this)) {
				 if (pm.isPluginEnabled(pl)) {
					 send("errors.enable.alreadyEnabled", cs, pl.getName());
					 return false;
				 } else {
					 pm.enablePlugin(pl);
					 send("infos.enable.success", cs, pl.getName());
					 return true;
				 }
			 } else {
				 send("errors.try.self", cs, pl.getName());
				 return false;
			 }
}
	  boolean disable(CommandSender cs, String s) {
		 PluginManager pm = Bukkit.getPluginManager();
		 Plugin pl = pm.getPlugin(s);
		 if (pl != null) {
			 if (!(pl == this)) {
			 if (!pm.isPluginEnabled(pl)) {
				 send("errors.enable.alreadyDisabled", cs, pl.getName());
				 return false;
			 } else {
				 pm.disablePlugin(pl);
				 send("infos.disable.success", cs, pl.getName());
				 return true;
			 }
		 } else {
			 send("errors.try.self", cs, pl.getName());
			 return false;
		 }
	} else {
		 send("errors.plugin.doesntExist", cs, s);
		 return false;
	 }
}
	  private void load(CommandSender cs, String s) {
				 PluginManager pm = Bukkit.getPluginManager();
				 Plugin pl = pm.getPlugin(s);
					 if (!(pl == this)) {
					 if (pm.isPluginEnabled(pl)) {
						 send("errors.enable.alreadyEnabled", cs, pl.getName());
					 } else {
						 loadPlugin(s, null, true, cs);
					 }
				 } else {
					 send("errors.try.self", cs, pl.getName());
				 }
	  }
	  private void unload(CommandSender cs, String s) {
			 PluginManager pm = Bukkit.getPluginManager();
			 Plugin pl = pm.getPlugin(s);
			 if (pl != null) {
				 if (!(pl == this)) {
				 if (!pm.isPluginEnabled(pl)) {
					 send("errors.enable.alreadyDisabled", cs, pl.getName());
				 } else {
					 try {
						unloadPlugin(s, true, cs);
					} catch (Exception e) {
						send("errors.unexpected", cs, s);
					}
				 }
			 } else {
				 send("errors.try.self", cs, pl.getName());
			 }
		} else {
			 send("errors.plugin.doesntExist", cs, s);
		 }
	  }
	  private void help(Player p) {
	        p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
	        p.sendMessage(ChatColor.BLUE + "         "+ pluginTag +" par "+pdfFile.getAuthors()+" !");
	        p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
	        p.sendMessage(ChatColor.RED + "  - fais /plm help pour afficher ce message.");
	        p.sendMessage(ChatColor.RED + "  - fais /plm enable <plugin> pour activer un plugin.");
	        p.sendMessage(ChatColor.RED + "  - fais /plm disable <plugin> pour désactiver un plugin.");
	        p.sendMessage(ChatColor.RED + "  - fais /plm load <plugin> pour charger un plugin.");
	        p.sendMessage(ChatColor.RED + "  - fais /plm unload <plugin> pour décharger un plugin.");
	        p.sendMessage(ChatColor.RED + "  - fais /plm reload [<soft|hard> <plugin>] pour recharger un plugin ou "+ChatColor.stripColor(pluginTag)+".");
	        p.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
	  }
	  private void reload(CommandSender cs, String s) {
			 PluginManager pm = Bukkit.getPluginManager();
		 if (s == null || pm.getPlugin(s) == this) {
			  reloadConfiguration();
			  	loadConfiguration();
			  		send("infos.plugin.reload", cs, null);
		 } else {
			 Plugin pl = pm.getPlugin(s);
			 if (pl != null) {
					 try {
						if (pl.isEnabled()) pm.disablePlugin(pl);
						pm.enablePlugin(pl);
						send("%reload_mode%=soft|infos.reload.success", cs, s);
					} catch (Exception e) {
						send("errors.unexpected", cs, s);
						e.printStackTrace();
					}
			 } else {
				 send("errors.plugin.doesntExist", cs, s);
			 }
		 }
	  }
	  private void reloadPl(CommandSender cs, String s) {
			 PluginManager pm = Bukkit.getPluginManager();
		 if (s == null || pm.getPlugin(s) == this) {
			  	loadConfiguration();
			  reloadConfiguration();
			  		send("infos.plugin.reload", cs, null);
		 } else {
			 Plugin pl = pm.getPlugin(s);
			 if (pl != null) {
					 try {
						reloadPlugin(pl.getName(), true, cs);
					} catch (Exception e) {
						send("errors.unexpected", cs, s);
						e.printStackTrace();
					}
			 } else {
				 send("errors.plugin.doesntExist", cs, s);
			 }
		 }
	  }
	  
  @SuppressWarnings("rawtypes") 
  boolean unloadPlugin(String pluginName, boolean errorHandling, CommandSender sender)
    throws Exception
  {
    PluginManager manager = getServer().getPluginManager();
    SimplePluginManager spmanager = (SimplePluginManager)manager;

    if (spmanager != null) {
      Field pluginsField = spmanager.getClass().getDeclaredField("plugins");
      pluginsField.setAccessible(true);
      List plugins = (List)pluginsField.get(spmanager);

      Field lookupNamesField = spmanager.getClass().getDeclaredField("lookupNames");
      lookupNamesField.setAccessible(true);
      Map lookupNames = (Map)lookupNamesField.get(spmanager);

      Field commandMapField = spmanager.getClass().getDeclaredField("commandMap");
      commandMapField.setAccessible(true);
      SimpleCommandMap commandMap = (SimpleCommandMap)commandMapField.get(spmanager);

      Field knownCommandsField = null;
      Map knownCommands = null;

      if (commandMap != null) {
        knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        knownCommands = (Map)knownCommandsField.get(commandMap);
      }
      Iterator it;
      for (Plugin plugin : manager.getPlugins())
        if (plugin.getDescription().getName().equalsIgnoreCase(pluginName)) {
          manager.disablePlugin(plugin);

          if ((plugins != null) && (plugins.contains(plugin))) {
            plugins.remove(plugin);
          }

          if ((lookupNames != null) && (lookupNames.containsKey(pluginName))) {
            lookupNames.remove(pluginName);
          }

          if (commandMap != null)
            for (it = knownCommands.entrySet().iterator(); it.hasNext(); ) {
              Map.Entry entry = (Map.Entry)it.next();

              if ((entry.getValue() instanceof PluginCommand)) {
                PluginCommand command = (PluginCommand)entry.getValue();

                if (command.getPlugin() == plugin) {
                  command.unregister(commandMap);
                  it.remove();
                }
              }
            }
        }
    }
    else
    {
      if (errorHandling) {
        send("errors.unexpected", sender, null);
      }
      return true;
    }

    if (((sender instanceof Player)) && (!errorHandling)) {
      send(sender.getName() + " has unloaded " + pluginName + ".", null, null);
    }

    if (errorHandling) {
      send("infos.unload.success", sender, pluginName);
    }

    return true;
  }
  boolean loadPlugin(String pluginName, String dir, boolean errorHandling, CommandSender sender)
  {
    try
    {
      PluginManager manager = getServer().getPluginManager();
      Plugin plugin = null;
      if (dir == null) plugin = manager.loadPlugin(new File("plugins", pluginName + ".jar"));
      else {
    	  plugin = manager.loadPlugin(new File(dir, pluginName + ".jar"));
      }

      if (plugin == null) {
        if (errorHandling) {
          send("errors.plugin.doesntExist", sender, pluginName);
        }
        return false;
      }

      plugin.onLoad();
      manager.enablePlugin(plugin);
    } catch (Exception e) {
      if (errorHandling) {
        send("errors.unexpected", sender, pluginName);
      }

      return false;
    }


    if (errorHandling) {
      send("infos.load.success", sender, pluginName);
    }

    return true;
  }
  boolean reloadPlugin(String pluginName, boolean errorHandling, CommandSender sender)
    throws Exception
  {
    boolean unload = unloadPlugin(pluginName, false, sender);
    boolean load = loadPlugin(pluginName, null, false, sender);

    if ((sender instanceof Player)) {
      send(sender.getName() + " reloaded " + pluginName + ".", null, null);
    }

    if ((unload) && (load)) {
      if (errorHandling)
    	  send("%reload_mode%=hard|infos.reload.success", sender, pluginName);
    }
    else {
      if (errorHandling) {
        send("errors.unexpected", sender, pluginName);
      }

      return false;
    }

    return true;
  }
  
  private String getMessage(String path) {
	  String msg = getConfig().getString(path).contains("&") ? ChatColor.translateAlternateColorCodes('&', getConfig().getString(path)) : getConfig().getString(path);
	  msg = msg.replaceAll("%pluginName%", pdfFile.getName()).replaceAll("%pluginVersion%", pdfFile.getVersion()).replaceAll("%author%", "Burn38");
	  return msg;
  }
  private void send(String message, CommandSender sender, String plugin)
  {
    if (sender == null || !(sender instanceof Player))
      log.info(message);
    else
      if (message.startsWith("%reload_mode%=")) {
    	  String type = message.startsWith("%reload_mode%=hard|") ? "`HARD`" : message.startsWith("%reload_mode%=soft|") ? "`SOFT`" : "`ÙNKNÒWN";
    	  message = message.substring(type == "`HARD`" ? "%reload_mode%=hard|".length() : type == "`SOFT`" ? "%reload_mode%=soft|".length() : 0, message.length());
    	  sender.sendMessage(pluginTag+getMessage("messages."+message).replaceAll("%sender%", sender.getName()).replaceAll("%plugin%", plugin).replaceAll("%reload_mode%", type));
      }
      else if (getMessage("messages."+message) != null) sender.sendMessage(pluginTag+getMessage("messages."+message).replaceAll("%sender%", sender.getName()).replaceAll("%plugin%", plugin));
      else sender.sendMessage(ChatColor.stripColor(pluginTag)+" messages."+message);
  }

/**  public PluginManagerAPI getApi() {
	  if (api != null) api = new PluginManagerAPI(this);
	  return api;
  }**/
}