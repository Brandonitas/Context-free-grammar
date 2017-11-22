
import java.util.Queue;
import java.util.ArrayList;
import java.util.Stack;

public class PDATransition {
    String read;
    String pop;
    String push="";
    State destination;
    State origin;

    public PDATransition(CFGTransition c, State origin){
        read = "\u03BB"; //lambda
        pop = c.left;
        this.origin=origin;
        destination = origin;

        for(int i=c.right.size()-1;i>=0;i--){
            push+=c.right.get(i);
        }
    }

    //lambda,a,a
    public PDATransition(String terminal, State origin){
        read = terminal;
        pop=terminal;
        push="\u03BB"; //lambda
        this.origin=origin;
        destination = origin;

    }

    //start to loop & loop to accept
    public PDATransition(State origin, State destination, String starting){
        if(origin.name.equals("start")){
            read = "\u03BB"; //lambda
            pop = "\u03BB"; //lambda
            push = starting;
            this.origin=origin;
            this.destination=destination;
        }else{
            read = "\u03BB"; //lambda
            pop = "Z";
            push ="\u03BB"; //lambda
            this.origin=origin;
            this.destination=destination;
        }
    }

    public PDATransition(String read){
        this.read = read;
    }

    public State run(Queue<Character> q, Stack<String> s){
        if(!pop.equals("\u03BB")){
            s.pop();
        }
        if(!read.equals("\u03BB")){
            q.remove();
        }
        if(!push.equals("\u03BB")){
            for(char it:push.toCharArray()){
                s.push(String.valueOf(it));
            }
        }

        return destination;
    }


    public static ArrayList<PDATransition> terminalTransitions(ArrayList<PDATransition> input){
        ArrayList<PDATransition> res = new ArrayList<PDATransition>();
        for(PDATransition pdaTransition:input){
            if(!pdaTransition.read.equals("\u03BB")){
                res.add(pdaTransition);
            }
        }
        return res;
    }

    public static ArrayList<PDATransition> usefulTransitions(ArrayList<PDATransition> input, Stack<String> stack, Queue<Character> tape){
        //busca en todas las transiciones
        ArrayList<PDATransition> res = new ArrayList<PDATransition>();
        for(PDATransition pdaTransition:input){
            //lo que tengo encima de mi stack es igual a lo que va a leer del stack la transicion
            if(pdaTransition.pop.equals(stack.peek())){

                if(pdaTransition.push.equals("\u03BB")&&tape.isEmpty()){
                    res.add(pdaTransition);
                }
                //si es terminal tiene que ser el mismo que tengo en mi tape
                else if((Character.isLowerCase(pdaTransition.push.charAt(0)))&&(String.valueOf(pdaTransition.push.charAt(0)).equals(String.valueOf(tape.peek())))&&(works(tape,pdaTransition))){ //SI LO QUE TENGO QUE SACAR DE LA TRANSI ES UN TERMIAL TIENE QUE SER IGUAL A LO QUE VOY A LEER DEL TAPE
                    res.add(pdaTransition);
                //si no es terminal debe de contener al menos un terminal de los que tengo en mi tape
                }else if((!Character.isLowerCase(pdaTransition.push.charAt(0)))&&(works(tape,pdaTransition))){//SI LO QUE TENGO QUE METER ES UN NONTERMIAL ENTRA
                    res.add(pdaTransition);
                }else{
                    //Si son puros nonterminals
                    //En caso que no tenga terminales en el top of the stack, todos los caracteres deben ser nonterminals
                    boolean accept=false;
                    for (char it:pdaTransition.push.toCharArray()){
                        if(Character.isUpperCase(it)){
                            accept=true&&accept;
                        }
                    }
                    res.add(pdaTransition);
                }
            }
        }
        return res;
    }

    public static boolean works(Queue<Character> tape, PDATransition trans){
        //busca si en mi tape tengo al menos un terminal que tiene la transicion
        boolean hasTerminals=false;
        for (char peek:trans.push.toCharArray()){
            if(Character.isLowerCase(peek)){
                hasTerminals=true;
            }
        }
        if (hasTerminals) {
            for (char cha : tape) {
                for (char peek : trans.push.toCharArray()) {
                    if (cha==peek) {
                        return true;
                    }
                }
            }
            return false;
        }else {
            return true;
        }
    }

    public static boolean hasLambdaTransitions(ArrayList<PDATransition> input,  Stack<String> stack){
        for (PDATransition transition : input) {
            if (Character.isUpperCase(transition.pop.charAt(0)) && transition.pop.equals(stack.peek().toString()) && transition.push.equals("\u03BB")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "( "+pop+", "+push+" )";
    }


}