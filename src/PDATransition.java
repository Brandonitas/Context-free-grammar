import java.util.ArrayList;

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
            pop = "Z0";
            push ="\u03BB"; //lambda
            this.origin=origin;
            this.destination=destination;
        }
    }
}