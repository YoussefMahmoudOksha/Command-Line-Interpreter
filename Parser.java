import java.util.*;
public class Parser {
    private String commandName;
    private String[] args = new String[10];
    public boolean parse(String input) {
        if (!input.isEmpty()) {
            List<String> tokens = new ArrayList<>();
            Scanner scanner = new Scanner(input);
            scanner.useDelimiter("\"[^\"]*\"|\\s+");
            while (scanner.hasNext()) {
                String token = scanner.next().replaceAll("\"", "");
                if (!token.isEmpty()) {
                    tokens.add(token);
                }
            }

            if (!tokens.isEmpty()) {
                commandName = tokens.get(0);
                tokens.remove(0);
                args = tokens.toArray(new String[0]);
                return true;
            }
        }
        return false;
}
    public String getCommandName(){
        return commandName;
    }
    public String[] getArgs(){
        return args;
    }
}
