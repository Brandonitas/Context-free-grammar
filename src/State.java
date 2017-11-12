import java.util.ArrayList;

public class State {
    String name;
    ArrayList<PDATransition> transitions= new ArrayList<PDATransition>();

    public State(String s){
        name=s;
    }
}
