# SpigotCommandHandler
With this Command Handler, you can create new commands, just like you would create a new Script in Unity3D!


# Dillinger

How to import to your project.

Create a new class in your project named "CommandHandler".

After thatis done, at your Main class, add this to your onEnable() method.

```java
public void onEnable() {
    CommandHandler.setAlternatives(this);
    //Your Code
}
```

Afterwards, go to your onCommand(...) method, and replace it with this:
```java
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    new CommandHandler(sender, label, args);
    return true;
}
```

Assuming you have already copied the CommandHandler from this project to your CommandHandler class, change the Package, PACKAGE_PATH and COMMAND_PREFIX. The PACKAGE_PATH is the path to the package that contains all the Commands, and COMMAND_PREFIX is the prefix given to all Commands.

For example, if you set PACKAGE_PATH = "com.example.mycommands" and COMMAND_PREFIX = "MyCommands", the class for the command "/heal" would be at
com.example.mycommands.MyCommandsheal. 

All commands that the are executed, automatically find the correct alias, and the all the command labels are converted to lowercase!

After you have done those above, all you have to do is extend CommandInfo, and you are good to go! 

```java
public class Commandcommandhandler extends CommandInfo {

	void Run() {
		player.setHealth(20.0);
		player.sendMessage("You have been healed!");
	}
	
	void Run(String[] args) {
		Player playerToHeal = Bukkit.getPlayer(args[0]);
		if(playerToHeal == null) {
			player.sendMessage("That player does not exist!");
		} 
		playerToHeal.setHealth(20.0);
		player.sendMessage("That player has been healed!");
		playerToHeal.sendMessage("You have been healed!");
	}
	
	void RunConsole() {
		sender.sendMessage("You need to be a player to run this command!");
	}
	
	//RunConsole(String[] args) is missing. It will default to whatever the method has inside the CommandInfo class!
}
```

