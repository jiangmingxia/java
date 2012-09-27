package com.hp.jmx.cmd;

public class CommandFactory {
	public static Command getCommand(String commandName){
		if (commandName.equals(CreateTSCommand.command_name)) return new CreateTSCommand();
		if (commandName.equals(LogCommand.command_name)) return new LogCommand();
		CommandOutput.errorOutput("Command "+commandName+" does not exist.");
		return null;
	}
	

}
