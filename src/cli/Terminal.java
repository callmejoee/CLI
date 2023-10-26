/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cli;

import java.io.File;
import java.io.*;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Seif
 */
public class Terminal {
    Parser parser;

    public Terminal() {
        this.parser = new Parser();
    }

    // __________________________________________________
    public void mkdir(String[] args) {
        for (String arg : args) {
            File dir = new File(arg);
            if (!dir.exists()) {
                dir.mkdirs();
            } else {
                System.out.println("Directory " + arg + " already exists");
            }
        }
    }

    // __________________________________________________
    public void rmdir(String arg) {
        if (arg.equals("*")) {
            File currentDirs = new File(System.getProperty("user.dir"));
            removeAll(currentDirs);
        } else if (arg.length() == 0) {
            System.err.println("Usage: rmdir <directory>");
        } else {
            File dir = new File(arg);

            if (dir.exists() && dir.isDirectory()) {
                if (isEmpty(dir)) {
                    dir.delete();
                } else {
                    System.out.println("Directory \"" + dir + "\" isn't empty");
                }
            } else {
                System.out.println("Directory does not exist: " + dir.getAbsolutePath());
            }
        }

    }

    // ++++++++++++
    private boolean isEmpty(File directory) {
        File[] files = directory.listFiles();
        if (files != null && files.length == 0)
            return true;
        return false;
    }

    // +++++++++++++
    private void removeAll(File directory) {
        File[] subDirs = directory.listFiles();
        if (subDirs != null) {
            for (File subDir : subDirs) {
                if (subDir.isDirectory() && isEmpty(subDir)) {
                    subDir.delete();
                }
            }
        }
    }

    // __________________________________________________
    public void cp(String source, String destination) {
        File src = new File(source);
        File dest = new File(destination);

        if (src.exists() && src.isFile()) {
            try (InputStream inputStream = new FileInputStream(src);
                    OutputStream outputStream = new FileOutputStream(dest)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                System.out.println("Failed to copy file: " + e.getMessage());
            }
        } else {
            System.out.println("Source file \"" + source + "\" does not exist: ");
        }
    }

    // __________________________________________________
    public void touch(String arg) {
        File file = new File(arg);
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                System.out.println("File \"" + arg + "\" already exists: ");
            }
        } catch (IOException e) {
            System.out.println("Failed to create file: " + arg);
        }
    }

    // *___________________________________________________
    public void rm(String fileName) throws CustomException {
        File file = new File(fileName);
        if (!file.isFile())
            throw new CustomException("No Such File!");
        if (!file.delete())
            throw new CustomException("Can't Delete File!");
    }

    // *___________________________________________________
    public void cat(String[] fileNames) throws CustomException {
        String result = "";
        FileManager fileManager = new FileManager();
        for (String fileName : fileNames) {
            result += fileManager.read(fileName);
        }
        System.out.println(result);
    }

    // *___________________________________________________
    public void wc(String fileName) throws CustomException {
        FileManager fileManager = new FileManager();
        String fileContent = fileManager.read(fileName);
        int lineCount = fileContent.split("\n").length;
        int wordCount = 0;
        for (String word : fileContent.replace("\n", " ").split(" ")) {
            if (word.strip().length() > 0) {
                wordCount++;
            }
        }
        int characterCount = fileContent.replace(" ", "").replace("\n", "").length();
        System.out.println(String.format("%d %d %d %s", lineCount, wordCount, characterCount, fileName));
    }

    public void chooseCommandAction(String command, String[] args) throws CustomException {
        switch (command) {
            case "mkdir":
                mkdir(args);
                break;
            case "rmdir":
                rmdir(args[0]);
                break;
            case "touch":
                touch(args[0]);
                break;
            case "cp":
                cp(args[0], args[1]);
                break;
            case "rm":
                rm(args[0]);
                break;
            case "cat":
                cat(args);
                break;
            case "wc":
                wc(args[0]);
                break;
            default:
                System.out.println("Command \"" + command + "\" not recognized.");
        }
    }

    // ___________________________________________________
    public static void main(String[] args) {
        Terminal terminal = new Terminal();

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("> ");
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                System.out.println("GoodBye :)");
                break;
            }
            if (terminal.parser.parse(input)) {
                String command = terminal.parser.getCommandName();
                String[] commandArgs = terminal.parser.getArgs();
                try {
                    terminal.chooseCommandAction(command, commandArgs);
                } catch (ArrayIndexOutOfBoundsException e) {
                    if (command.equals("mkdir"))
                        System.err.println("Usage: mkdir <directory>");
                    else if (command.equals("rmdir"))
                        System.err.println("Usage: rmdir <directory>");
                    else if (command.equals("cp"))
                        System.err.println("Usage: cp <source> <destination>");
                    else if (command.equals("touch"))
                        System.err.println("Usage: touch <Filename>");
                    else if (command.equals("rm"))
                        System.err.println("Usage: rm <Filename>");
                    else if (command.equals("cat"))
                        System.err.println("Usage: cat <Filename> <Filename2 (optional)>");
                    else if (command.equals("wc"))
                        System.err.println("Usage: wc <Filename>");
                } catch (CustomException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Invalid input.");
            }
        }
    }
}
