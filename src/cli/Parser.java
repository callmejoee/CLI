/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cli;

/**
 *
 * @author Seif
 */
public class Parser {
    String commandName;
    String[] args;
       
    public Parser() {
        this.commandName = "";
        this.args = new String[0];
    }

    
    public boolean parse(String input) {
        String[] parts = input.trim().split(" ");
        if (parts.length == 0) {
            return false;
        }
        commandName = parts[0];
        args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        return true;
    }
    
    public String getCommandName() {        
        return commandName;
    }
    
    
    public String[] getArgs() {        
        return args;
    }
}