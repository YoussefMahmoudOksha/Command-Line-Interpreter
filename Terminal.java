import java.io.File;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Comparator;
import java.io.IOException;
import java.util.Objects;
import java.nio.file.Files;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.BufferedReader;
import java.io.FileReader;

public class Terminal {
    static public Parser parser = new Parser();
    static public Terminal terminal = new Terminal();
    static public Boolean play = true;

    private static Path currentDirectory = Paths.get("").toAbsolutePath();


    public void echo(){
        for (int i = 0; i < parser.getArgs().length; i++){
            if (parser.getArgs()[i] != null){
                System.out.print(parser.getArgs()[i]);
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    public String pwd(){
        return currentDirectory.toString();
    }



    public void ls() {
        File dir = new File(currentDirectory.toString());
        File[] files = dir.listFiles();
        assert files != null;
        Arrays.sort(files, Comparator.comparing(File::getName));

        for (File file : files) {
            System.out.println(file.getName());
        }

    }

    public void lsR() {
        File dir = new File(currentDirectory.toString());
        File[] files = dir.listFiles();
        assert files != null;
        Arrays.sort(files, Comparator.comparing(File::getName).reversed());
        for (File file : files) {
            System.out.println(file.getName());
        }

    }

    public void touch(String fileName) {
        try {
            File file = new File(currentDirectory + "/" + fileName);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file.");
        }
    }

    public void cp(String sourceFile, String destinationFile) {
        try {
            Files.copy(Paths.get(sourceFile), Paths.get(destinationFile), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while copying the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cpR(String sourceDir, String destinationDir) {
        if (Objects.isNull(sourceDir) || Objects.isNull(destinationDir)) {
            System.out.println("Source or destination path is null.");
            return;
        }

        try {
            Path src = Paths.get(sourceDir);
            Path dest = Paths.get(destinationDir);

            Files.walk(src)
                    .forEach(source -> {
                        try {
                            if (source != null) {
                                Path newDestination = dest.resolve(src.relativize(source));

                                if (newDestination != null) {
                                    if (Files.exists(newDestination)) {
                                        System.out.println("Destination already exists: " + newDestination);
                                    } else {
                                        Files.createDirectories(newDestination.getParent());
                                        Files.copy(source, newDestination, StandardCopyOption.REPLACE_EXISTING);
                                    }
                                } else {
                                    System.out.println("New destination path is null.");
                                }
                            } else {
                                System.out.println("Source path is null.");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("An error occurred while copying the directory.");
                        }
                    });

            System.out.println("Directory copied successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while copying the directory.");
        }
    }

    public void cd(String directory) {
        try {
            Path newDir;

            if (directory.equals("..")) {
                // Case 2: cd takes 1 argument which is ".." and changes the current directory to the previous directory.
                newDir = currentDirectory.getParent();
            } else {
                // Case 3: cd takes 1 argument, either full path or relative path, and changes the current path to that path.
                newDir = currentDirectory.resolve(directory).normalize();
            }

            if (Files.exists(newDir) && Files.isDirectory(newDir)) {
                currentDirectory = newDir;
                System.out.println("Current directory changed to: " + newDir);
            } else {
                System.out.println("Invalid directory: " + newDir);
            }
        } catch (InvalidPathException e) {
            System.out.println("Invalid path: " + e.getMessage());
        }
    }

    public void cd(){
        currentDirectory = Paths.get(System.getProperty("user.home"));
    }

    public void rm(String sourcePath) {

        File file= new File(sourcePath);
        if (!file.exists()) {
            System.out.println("File not exist");
        }else file.delete();
    }

    public void rmdir(String argument) {
        File directory = new File(argument);

        if (argument.equals("*")) {
            File currentDirectory = new File(System.getProperty("user.dir"));
            File[] subDirectories = currentDirectory.listFiles(File::isDirectory);

            if (subDirectories != null) {
                for (File dir : subDirectories) {
                    if (isDirectoryEmpty(dir)) {
                        if (dir.delete()) {
                            System.out.println("Directory deleted successfully: " + dir.getPath());
                        } else {
                            System.out.println("Failed to delete directory: " + dir.getPath());
                        }
                    }
                }
            }
        } else {
            if (directory.exists() && directory.isDirectory()) {
                if (isDirectoryEmpty(directory)) {
                    if (directory.delete()) {
                        System.out.println("Directory deleted successfully: " + directory.getPath());
                    } else {
                        System.out.println("Failed to delete directory: " + directory.getPath());
                    }
                } else {
                    System.out.println("The specified directory is not empty or doesn't exist.");
                }
            } else {
                System.out.println("Directory does not exist or is not a directory.");
            }
        }
    }

    public static boolean isDirectoryEmpty(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            return files != null && files.length == 0;
        }
        return false;
    }

    public void wc(String fileName) {
        try {
            Path filePath = currentDirectory.resolve(fileName);
            String fileContent = new String(Files.readAllBytes(filePath));

            long lineCount = Arrays.stream(fileContent.split("\r\n|\r|\n")).count();
            long wordCount = Arrays.stream(fileContent.split("\\s+")).filter(word -> !word.isEmpty()).count();
            long characterCount = fileContent.length();

            System.out.println(lineCount + " " + wordCount + " " + characterCount + " " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred while processing the file: " + e.getMessage());
        }
    }

    public void cat(String[] fileNames) {
        if (fileNames.length == 1) {
            File file = new File(fileNames[0]);
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading the file: " + e.getMessage());
                }
            } else {
                System.out.println("File does not exist or is not a regular file.");
            }
        } else if (fileNames.length > 1) {
            for (String fileName : fileNames) {
                File file = new File(fileName);
                if (file.exists() && file.isFile()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading the file: " + e.getMessage());
                    }
                } else {
                    System.out.println("File " + fileName + " does not exist or is not a regular file.");
                }
            }
        } else {
            System.out.println("Invalid 'cat' command. Usage: cat <filename> OR cat <file1> <file2> ...");
        }
    }


    public void chooseCommandAction() {
        String command = parser.getCommandName();
        String[] args = parser.getArgs();

        if (Objects.equals(command, "echo")) {
            echo();
        } else if (Objects.equals(command, "exit")) {
            play = false;
        } else if (Objects.equals(command, "pwd")) {
            System.out.println(pwd());
        } else if (Objects.equals(command, "ls")) {
            if (args.length == 1 && Objects.equals(args[0], "-r")) {
                lsR();
            } else {
                ls();
            }
        }else if (Objects.equals(command, "touch")) {
            if (args.length == 1 && args[0] != null) {
                touch(args[0]);
            } else {
                System.out.println("Invalid 'touch' command. Usage: touch <filename>");
            }
        }else if (Objects.equals(command, "cp")) {
            if (args.length > 1 && Objects.equals(args[0], "-r")) {
                if (args.length == 3) {
                    String sourcePath = args[1];
                    String destinationPath = args[2];

                    cpR(sourcePath, destinationPath);
                } else {
                    System.out.println("Invalid 'cp -r' command. Usage: cp -r <sourceDirectory> <destinationDirectory>");
                }
            } else {
                if (args.length == 2) {
                    cp(args[0], args[1]);
                } else {
                    System.out.println("Invalid 'cp' command. Usage: cp <sourceFile> <destinationFile>");
                }
            }
        }else if (Objects.equals(command, "cd")) {

            if (args.length == 0){
                cd();
            } else if (args.length == 1 && args[0] != null) {
                cd(args[0]);
            }

        }else if (Objects.equals(command, "rm")) {
            if (args.length == 1 && args[0] != null) {
                rm(args[0]);
            }else if(args.length == 0) {
                System.out.println("Invalid 'rm' command. Usage: rm <file name> ");
            }
        }else if (Objects.equals(command, "rmdir")) {
            if (args.length == 1 && args[0] != null) {
                rmdir(args[0]);
            }else if(args.length == 0) {
                System.out.println("Invalid 'rmdir' command. Usage: rmdir <*> or <path directory> ");
            }
        }else if (Objects.equals(command, "wc")) {
            if (args.length == 1 && args[0] != null) {
                wc(args[0]);
            }else if(args.length == 0) {
                System.out.println("Invalid 'wc' command. Usage: wc <file name>");
            }
        }else if (Objects.equals(command, "cat")) {
            cat(args);
        }else {
            System.out.println("Command not found");
        }
    }


    public static void main(String[] args) {

        while (play){
            System.out.print(">");
            Scanner scanner = new Scanner(System.in);
            String Command = scanner.nextLine();

            if(parser.parse(Command)){
                terminal.chooseCommandAction();
            }
        }


    }
}
