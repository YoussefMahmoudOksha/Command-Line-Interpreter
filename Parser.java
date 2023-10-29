import java.util.*;
public class Parser {
    private String commandName;
    private final String[] args = new String[10];
    public boolean parse(String input){
        if (!input.isEmpty()){
            int i = 0;
            Scanner scanner = new Scanner(input);
            commandName = scanner.next();
            while (scanner.hasNext()){
                args[i] =(scanner.next());
                i++;
            }
            return true;
        }else{
            return false;
        }

    }
    public String getCommandName(){
        return commandName;
    }
    public String[] getArgs(){
        return args;
    }
}
