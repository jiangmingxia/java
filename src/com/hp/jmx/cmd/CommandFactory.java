package com.hp.jmx.cmd;

public class CommandFactory {
	public static Command getCommand(String commandName){
		if (commandName.equals(CreateTSCommand.command_name)) return new CreateTSCommand();
		return null;
	}
	

}
