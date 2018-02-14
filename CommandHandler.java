/*
 * This was created by KKosyfarinis. 
 * Feel free to modify it as much as you want, as long as you leave this comment at the top of the file
 * https://github.com/KKosyfarinis
 */

package me.kkosyfarinis.spigot.commandhandler.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandHandler {
	
	private static final String PACKAGE_PATH = "me.kkosyfarinis.spigot.commandhandler.commands";
	private static final String COMMAND_PREFIX = "Command";

	private CommandSender sender;
	private String command, label;
	private String[] args;
	
	private HashMap<String, CommandClass> cmdClasses = new HashMap<>();
	
	private Class[] setParamsParameters = new Class[] {CommandSender.class, String.class, String.class, String[].class };
	private Class[] argsParam = new Class[] { String[].class };
	
    private static HashMap<String, String> cmdAlternatives = new HashMap<>();
    
    
    public CommandHandler(CommandSender sender, String label, String[] args) {
    	String command = getAlternative(label);
    	new CommandHandler(sender, command, label, args);
    }
	
	public CommandHandler(CommandSender sender, String command, String label, String[] args) {
		
		this.sender = sender;
		this.command = command;
		this.label = label;
		this.args = args;
		
		try {
		    Class<?> setupClass = Class.forName(PACKAGE_PATH + "." + COMMAND_PREFIX + label);
		    Object classInstance = setupClass.newInstance();
		    
		    
			execute();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private void execute() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		if(!cmdClasses.containsKey(command)) {
			addCommand(command);
		}
		
		Object classInstance = cmdClasses.get(command).cmdclass.newInstance();
		
		cmdClasses.get(command).setParams.invoke(classInstance, sender, command, label, args);
		
		switch(findCase()) {
		
		//Console no args
		case 1: {
			cmdClasses.get(command).runConsole.invoke(classInstance);
			break;
		}
		
		//Console args
		case 2: {
			cmdClasses.get(command).runConsoleArgs.invoke(classInstance, args);
			break;
		}
		
		//Player no args
		case 11: {
			cmdClasses.get(command).run.invoke(classInstance);
			break;
		}
		
		//Player args
		case 12: {
			cmdClasses.get(command).runArgs.invoke(classInstance, args);
			break;
		}
		}
	}
	
	private int findCase() {
		int _case = 0;
		if(sender instanceof Player) {
			_case += 10;
		}
		if(args.length > 0) {
			_case += 2;
		} else {
			_case += 1;
		}
		
		return _case;
		
	}
	
	private void addCommand(String name) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		CommandClass commandClass = new CommandClass();
		
		commandClass.cmdclass = Class.forName(PACKAGE_PATH + "." + COMMAND_PREFIX + name);
		commandClass.setParams = commandClass.cmdclass.getSuperclass().getDeclaredMethod("setParams", setParamsParameters);

		try {
			commandClass.run = commandClass.cmdclass.getDeclaredMethod("Run");
		} catch(NoSuchMethodException exception) {
			commandClass.run = commandClass.cmdclass.getSuperclass().getDeclaredMethod("Run");
		}
		
		try {
			commandClass.runArgs = commandClass.cmdclass.getDeclaredMethod("Run", argsParam);
		} catch(NoSuchMethodException exception) {
			commandClass.runArgs = commandClass.cmdclass.getSuperclass().getDeclaredMethod("Run", argsParam);	
		}

		try {
			commandClass.runConsole = commandClass.cmdclass.getDeclaredMethod("RunConsole");
		} catch(NoSuchMethodException exception) {
			commandClass.runConsole = commandClass.cmdclass.getSuperclass().getDeclaredMethod("RunConsole");	
		}
		
		try {
			commandClass.runConsoleArgs = commandClass.cmdclass.getDeclaredMethod("RunConsole", argsParam);
		} catch(NoSuchMethodException exception) {
			commandClass.runConsoleArgs = commandClass.cmdclass.getSuperclass().getDeclaredMethod("RunConsole", argsParam);	
		}
		
		cmdClasses.put(name, commandClass);
		
	}
	
	private class CommandClass {
		private Class<?> cmdclass;
		private Method setParams, run, runArgs, runConsole, runConsoleArgs;
	}
	
	public static void setAlternatives(Plugin plugin) {
        Map<String, Map<String, Object>> commands = plugin.getDescription().getCommands();
        for(String command : commands.keySet()) {
     
            Object aliases = commands.get(command).get("aliases");
            for(String alias : String.valueOf(aliases).split(",")) {
            	cmdAlternatives.put(alias.replace("[", "").replace("]", "").replace(" ", ""), command);
            }
            cmdAlternatives.put(command, command);
        }
	}
	
	private String getAlternative(String label) {
		return cmdAlternatives.get(label).toLowerCase();
	}
	
	public static String getPackagePath() {
		return PACKAGE_PATH;
	}
	
	public static String getCommandPrefix() {
		return COMMAND_PREFIX;
	}
	
	public static class CommandInfo {
		
		protected CommandSender sender;
		protected String command, label;
		protected String[] args;
		
		protected Player player = null;
		
		public void setParams(CommandSender sender, String command, String label, String[] args) {
			this.sender = sender;
			this.command = command;
			this.label = label;
			this.args = args;
			
			if(sender instanceof Player) {
				player = Bukkit.getPlayer(sender.getName());
			}
		}
		
		void Run() {
			sender.sendMessage("Default Message With Args!");
		}
		
		void Run(String[] args) {
			sender.sendMessage("Default Message!");
		}
		
		void RunConsole() {
			sender.sendMessage("Default Message!");
		}
		
		void RunConsole(String[] args) {
			sender.sendMessage("Default Message With Args!");
		}
	}
}
