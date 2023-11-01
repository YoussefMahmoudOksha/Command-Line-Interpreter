import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.nio.file.StandardCopyOption;

import java.util.Scanner;

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
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }



    public void ls() {
        File dir = new File(currentDirectory.toString());
        File[] files = dir.listFiles();
            Arrays.sort(files, Comparator.comparing(File::getName));

            for (File file : files) {
                System.out.println(file.getName());
            }

    }

    public void lsR() {
        File dir = new File(currentDirectory.toString());
        File[] files = dir.listFiles();
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
