import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Terminal {
    static public Parser parser = new Parser();
    static public Terminal terminal = new Terminal();
    static public Boolean play = true;

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
    public void chooseCommandAction(){
        String command = parser.getCommandName();
        if (Objects.equals(command, "echo")){
            terminal.echo();
        } else if (Objects.equals(command,"exit")) {
            play = false;
        } else if (Objects.equals(command,"pwd")) {
            System.out.println(terminal.pwd());
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