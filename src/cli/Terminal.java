/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cli;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import cli.CustomExceptions.CustomException;
import cli.FileManagers.FileManager;

/**
 *
 * @author Seif
 */
public class Terminal {
    Parser parser;

    public Terminal() {
        this.parser = new Parser();
    }

    public void echo (String[] args) {
        String str = String.join(" ", args);
        if (str.startsWith("\"") && str.endsWith("\"")) {
            str = str.substring(1, str.length()-1);
            System.out.println(str);
        }
        else {
            System.out.println(str);
        }
    }

    public void pwd() {
        Path path = Paths.get("").toAbsolutePath();
        System.out.println(path.toString());
    }

    public void cd(String[] args){
        if (args.length == 0)
        {
            String home_dir = System.getProperty("user.home");
            File dir = new File(home_dir);
            System.setProperty("user.dir", dir.getAbsolutePath());
        }
        else if (args[0].equals(".."))
        {
            File dir = new File("..");
            System.setProperty("user.dir", dir.getAbsolutePath());

        }
        else {
            File dir = new File(args[0]);
            System.setProperty("user.dir", dir.getAbsolutePath());

        }
    }

    public void ls(String[] args){
        File currentDir = new File(System.getProperty("user.dir"));
        String[] files = currentDir.list();

        if (args.length == 0)
            Arrays.sort(files);

        if (args.length == 1 && args[0].equals("-r"))
        {
            Collections.reverse(Arrays.asList(files));
        }

        for (String fileName : files) {
            System.out.println(fileName);
        }
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

    // __________________________________________________
    public void recursivePathFinder(String source, String destination) throws CustomException {
        File file = new File(source);
        if (!file.exists()) {
            throw new CustomException("Path Not Found.");
        }
        File[] subFiles = file.listFiles();
        for (File subFile : subFiles) {
            String newSource = source + "\\" + subFile.getName();
            String newDestination = destination + "\\" + subFile.getName();
            if (subFile.isFile()) {
                cp(newSource, newDestination);
            } else {
                mkdir(new String[] { newDestination });
                recursivePathFinder(newSource, newDestination);
            }

        }
    }
    // ?___________________________________________________

    public void cpr(String source, String destination) throws CustomException {
        String newDestinationParentFolder = destination + "\\" + source;
        mkdir(new String[] { newDestinationParentFolder });
        recursivePathFinder(source, newDestinationParentFolder);
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
    // *___________________________________________________

    public void chooseCommandAction(String command, String[] args) throws CustomException {
        if (command.equals("mkdir"))
        {
            if(args.length == 0)
                System.err.println("Usage: mkdir <directory>");
            else
                mkdir(args);
        }
        else if (command.equals("rmdir"))
            rmdir(args[0]);
        else if (command.equals("touch"))
            touch(args[0]);
        else if (command.equals("cp"))
            if (args[0].equals("-r"))
                cpr(args[1], args[2]);
            else
                cp(args[0], args[1]);

        else if (command.equals("rm"))
            rm(args[0]);
        else if (command.equals("cat"))
            cat(args);
        else if (command.equals("wc"))
            wc(args[0]);
        else if (command.equals("echo"))
            echo(args);
        else if (command.equals("pwd")) {
            pwd();
        } else if (command.equals("cd")) {
            cd(args);

        } else if (command.equals("ls")) {
            ls(args);
        } else
            System.out.println("Command \"" + command + "\" not recognized.");
    }

    // ___________________________________________________
    public static void main(String[] args) {
        Terminal terminal = new Terminal();

        Scanner scanner = new Scanner(System.in);
        while (true) {
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
                        System.err.println("Usage: cp '-r (recursive)' <source> <destination>");
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
        scanner.close();
    }
}