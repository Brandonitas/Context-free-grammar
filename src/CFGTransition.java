import java.util.ArrayList;

public class CFGTransition {
    String left;
    ArrayList<String> right = new ArrayList<>();

    public CFGTransition(String input){
        left=input.split("->")[0];
        for(char c:input.split("->")[1].toCharArray()) {
            right.add(String.valueOf(c));

        }
    }

}
