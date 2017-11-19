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
                //System.out.print(start.transitions.get(0)+"->");
            }else{
                if(stack.isEmpty()&&tape.isEmpty()){
                    current=accept;
                    break Steps;
                }else if(stack.isEmpty()){
                    break Steps;
                }

                if(stack.peek().equals(stack.peek().toLowerCase())){
                    boolean found=false;
                    for(PDATransition transition: PDATransition.terminalTransitions(current.transitions)){
                        if(tape.peek().toString().equals(transition.read)){
                            if(stack.peek().toString().equals(transition.pop)){
                                current=transition.run(tape,stack);
                                System.out.print(transition+"->");
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

                    for(PDATransition transition:PDATransition.usefulTransitions(current.transitions,stack.peek(),String.valueOf(tape.peek()))){//

                        //EVALUAMOS LAS POSIBLES

                        Queue<Character> newTape = new LinkedList<Character>();
                        for(char c:tape){
                            newTape.add(c);
                        }
                        Stack<String> stack1=new Stack<>();
                        for(String c:stack){
                            stack1.push(c);
                        }

                        ArrayList<PDATransition> targetTransitions = expandTree(newTape,stack1,transition);//BUSCA LA RAMA QUE TERMINE Y ME REGRESA LAS TRANSICIONES QUE HACE PARA TERMINAR

                        if(targetTransitions.size()!=0){//SI HUBO ALGUN CAMINO

                            for(PDATransition p:targetTransitions){
                                current=p.run(tape,stack);
                                System.out.print(p+"->");
                            }
                            found=true;
                            break;
                        }
                    }
                    if(!found){
                        break Steps;
                    }
                }


            }
        }
        return current.name.equals("accept");
    }

    public ArrayList<PDATransition> expandTree(Queue<Character> tape, Stack<String>stack, PDATransition pdaTransition){
        State current =pdaTransition.run(tape, stack);

        ArrayList<PDATransition> myTransitions = new ArrayList<PDATransition>();//LISTA DE POSIBLES TRANSICIONES QUE LLEGA AL FINAL
        myTransitions.add(pdaTransition);

        if(stack.peek().equals(stack.peek().toLowerCase())){
            boolean encontrado=false;
            for(PDATransition transition: PDATransition.terminalTransitions(current.transitions)){
                if(tape.peek().toString().equals(transition.read)){
                    if(stack.peek().toString().equals(transition.pop)){
                        current=transition.run(tape,stack);
                        myTransitions.add(transition);
                        //regreso las que sí me sirvieron
                        encontrado = true;
                        break;
                    }

                }
            }
        }else {
            boolean encontrado = false;
            //Buscar tran que nos sirve
            for(PDATransition transition: PDATransition.usefulTransitions(current.transitions,stack.peek(),String.valueOf(tape.peek()))){
                //EVALUAMOS LAS POSIBLES, LLAMO A LAS QUE ME SIGUEN A VER SI ENCUENTRO LA ULTIMA
                Queue<Character> newTape = new LinkedList<Character>();
                for(char c:tape){
                    newTape.add(c);
                }
                Stack<String> stack1=new Stack<>();

                for(String c:stack){
                    stack1.push(c);
                }


                ArrayList<PDATransition> targetTransitions = expandTree(newTape,stack1,transition);//TRANSICIONES QUE SÍ VAN A LLEGAR
                if(targetTransitions.size()!=0){
                    for(PDATransition p:targetTransitions){
                        myTransitions.add(p);//AGREGO A MI LISTA
                    }
                    encontrado=true;
                    break;
                }
            }
        }
        if((stack.size()>tape.size())&&(stack.size()==0)){
            return new ArrayList<PDATransition>();
        }else
            return myTransitions;
    }


}
