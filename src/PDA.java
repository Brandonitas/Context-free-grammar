import java.util.ArrayList;
import java.util.Stack;

public class PDA {
    String startingState;
    Stack<String> stack = new Stack<>();
    State start;
    State loop;
    State accept;
    ArrayList<State> states = new ArrayList<State>();

    public PDA(String startingState, ArrayList<CFGTransition> transitions, ArrayList<String> nonterminal, ArrayList<String> terminal) {
        loop = new State("loop");
        this.startingState = startingState;
        for(String t: nonterminal){
            State state = new State(t); //S,A,B
            for(CFGTransition c: transitions){
                if(c.left.equals(t)) {
                    state.transitions.add(new PDATransition(c, loop));
                }
            }

            states.add(state);

        }

        //LOOP
        for(State s: states){
            loop.transitions.addAll(s.transitions);
        }
        for(String s:terminal){
            loop.transitions.add(new PDATransition(s,loop));
        }

        //START

        start = new State("start");
        start.transitions.add(new PDATransition(start,loop));

        //ACCEPT

        accept = new State("accept");
        loop.transitions.add(new PDATransition(loop,accept));
        System.out.println();

    }

    public boolean run(String s){

        return true;
    }


}
