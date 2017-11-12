import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class PDA {
    String startingState;
    Stack<String> stack = new Stack<String>();
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
        State current = start;
        stack = new Stack<String>();
        Queue<Character> tape = new LinkedList<Character>();

        for(char c:s.toCharArray()){
            tape.add(c);
        }
        //
        Steps:
        while(!current.name.equals("accept")){
            if(current.name.equals("start")){
                current=start.transitions.get(0).run(tape,stack);
            }else{

                if(stack.peek().equals(stack.peek().toLowerCase())){
                    boolean found=false;
                    for(PDATransition transition: PDATransition.terminalTransitions(current.transitions)){
                        if(tape.peek().equals(transition.read)){
                            if(stack.peek().equals(transition.pop)){
                                current=transition.run(tape,stack);
                                found = true;
                                break;
                            }

                        }
                    }
                    if(!found){
                        break Steps;
                    }
                }else {
                    boolean found = false;
                    //Buscar tran que nos sirve
                    for(PDATransition transition: PDATransition.usefulTransitions(current.transitions,stack.peek(),String.valueOf(tape.peek()))){
                    //EVALUAMOS LAS POSIBLES


                    }
                    if(!found){
                        break Steps;
                    }
                }


            }
        }
        return current.name.equals("accept");
    }


}
