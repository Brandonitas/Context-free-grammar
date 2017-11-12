
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

    public PDATransition(String terminal, State origin){
        read = terminal;
        pop=terminal;
        push="\u03BB"; //lambda
        this.origin=origin;
        destination = origin;

    }

    public PDATransition(State origin, State destination){
        if(origin.name.equals("start")){
            read = "\u03BB"; //lambda
            pop = "\u03BB"; //lambda
            push = "S";
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

    public State run(Queue<Character> q, Stack<String> s){
        if(!pop.equals("\u03BB")){
            s.pop();
        }
        if(!read.equals("\u03BB")){
            q.remove();
        }
        if(!push.equals("\u03BB")){
            s.push(push);
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

    public static ArrayList<PDATransition> usefulTransitions(ArrayList<PDATransition> input, String target, String peek){
        ArrayList<PDATransition> res = new ArrayList<PDATransition>();
        for(PDATransition pdaTransition:input){
            if(pdaTransition.pop.equals(target)){
                if(Character.isLowerCase(pdaTransition.push.charAt(0))&&String.valueOf(pdaTransition.push.charAt(0)).equals(peek)){
                    res.add(pdaTransition);
                }else if(!Character.isLowerCase(pdaTransition.push.charAt(0))){
                    res.add(pdaTransition);
                }

            }
        }
        return res;

    }
}