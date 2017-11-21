import java.util.*;

public class PDA {
    String startingState;
    Stack<String> stack = new Stack<String>();
    State start;
    State loop;
    State accept;
    ArrayList<State> states = new ArrayList<State>();
    ArrayList<String> nonterminal = new ArrayList<>();

    //
    public PDA(String startingState, ArrayList<CFGTransition> transitions, ArrayList<String> nonterminal, ArrayList<String> terminal) {
        //pasar de mi gramatica a un PDA
        this.nonterminal=nonterminal;
        loop = new State("loop");
        this.startingState = startingState;
        //a cada nonterminal le agrego sus transiciones correspondientes
        for (String t : nonterminal) {
            State state = new State(t); //S,A,B
            for (CFGTransition c : transitions) {
                if (c.left.equals(t)) {
                    //las meto y les digo que van en loop
                    state.transitions.add(new PDATransition(c, loop));
                }
            }

            //Los agreo a mi lista de states
            states.add(state);
        }

        //LOOP
        //Agrego todos los estados a mi loop
        for (State s : states) {
            loop.transitions.addAll(s.transitions);
        }
        //agrego tambien mis transiciones lambda lambda, a,a
        for (String s : terminal) {
            loop.transitions.add(new PDATransition(s, loop));
        }

        //START
        start = new State("start");
        start.transitions.add(new PDATransition(start, loop, startingState));

        //ACCEPT
        accept = new State("accept");
        loop.transitions.add(new PDATransition(loop, accept, "Z"));
        System.out.println();
    }

    public boolean run(String s) {
        //pueden ser start, loop, accept
        State current = start;
        stack = new Stack<String>();
        Queue<Character> tape = new LinkedList<Character>();
        //lleno mi tape con mi string a leer
        for (char c : s.toCharArray()) {
            tape.add(c);
        }
        //
        Steps:
        while (!current.name.equals("accept")) {
            if (current.name.equals("start")) {
                //Cambio de start a loop y meto mi S al stack
                current = start.transitions.get(0).run(tape, stack);
                //Imprimir la primera
                String res="(";
                for (char c:tape) {
                    res+=c;
                }
                res+=",";
                for (int i=stack.size();i>0;i--) {
                    res+=stack.get(i-1);
                }
                System.out.print(res+")->");
            } else {
                if (stack.isEmpty() && tape.isEmpty()) {
                    current = accept;
                    break Steps;
                } else if (stack.isEmpty()) {
                    break Steps;
                }

                //encima de mi stack tengo un terminal debe ser el que estoy leyendo en tape
                //reviso que tenga terminal
                if (stack.peek().equals(stack.peek().toLowerCase())) {
                    boolean found = false;
                    //por cada transicion del estilo lamda,a,a, reviso que lo que lea del tape sea lo que tengo que leer del tape
                    //si si lo saco de mi tape y de mi stack y continuo
                    // si no, no es aceptado
                    for (PDATransition transition : PDATransition.terminalTransitions(current.transitions)) {
                        if (tape.peek().toString().equals(transition.read)) {
                            if (stack.peek().toString().equals(transition.pop)) {
                                current = transition.run(tape, stack);
                                System.out.print(transition + "->");
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        break Steps;
                    }
                } else {
                    //ABRO el arbol

                    boolean found = false;
                    //Buscar tran que nos sirve

                    ArrayList<PDATransition> pdaTransitions = PDATransition.usefulTransitions(current.transitions, stack, tape);
                    // si no encuentro transiciones que me sirvan no es aceptado
                    while (!found && !(pdaTransitions.isEmpty())) {//
                        PDATransition transition = pdaTransitions.remove(0);
                        //EVALUAMOS LAS POSIBLES

                        Stack<String> stack1 = new Stack<>();
                        for (String c : stack) {
                            stack1.push(c);
                        }

                        //parto mi stack en la cantidad de terminals que le sirven a mi transicion
                        Stack<String> stack2 = new Stack<>();
                        while ((!stack1.isEmpty()) && ((stack2.isEmpty()) || (!(stack1.peek().equals(stack2.peek())) && !(Character.isLowerCase(stack1.peek().charAt(0)))))) {
                            stack2.push(stack1.pop());
                        }

                        //si mi transicion necesita leer terminal que sean igual al que esta partiendo, contamos la cantidad indicada
                        int cont=0;
                        for(char it:transition.push.toCharArray()){
                            if(!(stack1.isEmpty())&&it==stack1.peek().charAt(0)){
                                cont++;
                            }
                        }

                        Queue<Character> newTape = new LinkedList<Character>();

                        for (char c : tape) {
                            //si mi tengo solo un caracter en mi transicion
                            if (Character.isLowerCase(transition.push.charAt(0)) && transition.push.length() == 1) {
                                newTape.add(c);
                                break;
                            }
                            //si mi stack esta vacio lo agrego a mi nueva tape para que vuelva a evaluar
                            if (stack1.isEmpty()) {
                                newTape.add(c);//SOLO CARACTERES QUE VAN AL ARBOL DE ESE LADO, ANTES DE MI TERMINAL
                            } else {
                                //si mi string es >1 entro al else
                                //agrego al nuevo tape hasta que sea igual al caracter por donde voy a partir
                                if (!(String.valueOf(c).equals(stack1.peek()))) {
                                    newTape.add(c);//SOLO CARACTERES QUE VAN AL ARBOL DE ESE LADO, ANTES DE MI TERMINAL
                                } else {
                                    //si leo un caracter igual al que uso para partir, debe ser la cantidad que mi transicion necesita
                                    if (cont > 0) {
                                        newTape.add(c);
                                        cont--;
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }


                        ArrayList<PDATransition> targetTransitions = expandTree(newTape, stack2, transition);//BUSCA LA RAMA QUE TERMINE Y ME REGRESA LAS TRANSICIONES QUE HACE PARA TERMINAR

                        if (targetTransitions.remove(targetTransitions.size() - 1).read.equals("F")) {//SI HUBO ALGUN CAMINO

                            for (PDATransition p : targetTransitions) {
                                current = p.run(tape, stack);
                                String res="(";
                                for (char c:tape) {
                                    res+=c;
                                }
                                res+=",";
                                for (int i=stack.size();i>0;i--) {
                                    res+=stack.get(i-1);
                                }
                                System.out.print(res+")->");
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        break Steps;
                    }
                }
            }
        }
        return current.name.equals("accept");
    }

    public ArrayList<PDATransition> expandTree(Queue<Character> tape, Stack<String> stack, PDATransition pdaTransition) {
        ArrayList<PDATransition> myTransitions = new ArrayList<PDATransition>();//LISTA DE POSIBLES TRANSICIONES QUE LLEGA AL FINAL
        State current = pdaTransition.run(tape, stack);
        myTransitions.add(pdaTransition);

        while ((!tape.isEmpty() && !stack.isEmpty()) && (stack.size() <= tape.size())) {
            if (stack.peek().equals(stack.peek().toLowerCase())) {
                boolean encontrado = false;
                for (PDATransition transition : PDATransition.terminalTransitions(current.transitions)) {
                    if (tape.peek().toString().equals(transition.read)) {
                        if (stack.peek().toString().equals(transition.pop)) {
                            current = transition.run(tape, stack);
                            myTransitions.add(transition);
                            //regreso las que sí me sirvieron
                            encontrado = true;
                            break;
                        }
                    }
                }
                if(!encontrado){
                    myTransitions.add(new PDATransition("N"));
                    return myTransitions;
                }
            } else {
                boolean encontrado = false;
                //Buscar tran que nos sirve

                ArrayList<PDATransition> pdaTransitions = PDATransition.usefulTransitions(current.transitions, stack, tape);
                while (!encontrado && !(pdaTransitions.isEmpty())) {
                    //EVALUAMOS LAS POSIBLES, LLAMO A LAS QUE ME SIGUEN A VER SI ENCUENTRO LA ULTIMA
                    PDATransition transition = pdaTransitions.remove(0);

                    Stack<String> stack1 = new Stack<>();
                    for (String c : stack) {
                        stack1.push(c);
                    }

                    Stack<String> stack2 = new Stack<>();

                    while ((!stack1.isEmpty()) && ((stack2.isEmpty()) || (!(stack1.peek().equals(stack2.peek())) && !(Character.isLowerCase(stack1.peek().charAt(0)))))) {
                        stack2.push(stack1.pop());
                    }

                    int cont=0;
                    for(char it:transition.push.toCharArray()){
                        if(!(stack1.isEmpty())&&it==stack1.peek().charAt(0)){
                            cont++;
                        }
                    }

                    Queue<Character> newTape = new LinkedList<Character>();
                    for (char c : tape) {
                        if(Character.isLowerCase(transition.push.charAt(0))&&transition.push.length()==1){
                            newTape.add(c);
                            break;
                        }
                        if (stack1.isEmpty()) {
                            newTape.add(c);//SOLO CARACTERES QUE VAN AL ARBOL DE ESE LADO, ANTES DE MI TERMINAL
                        } else {
                            if (!(String.valueOf(c).equals(stack1.peek()))) {
                                newTape.add(c);//SOLO CARACTERES QUE VAN AL ARBOL DE ESE LADO, ANTES DE MI TERMINAL
                            }else{
                                if(cont>0){
                                    newTape.add(c);
                                    cont--;
                                }else {
                                    break;
                                }
                            }
                        }

                    }

                    ArrayList<PDATransition> targetTransitions = expandTree(newTape, stack2, transition);//TRANSICIONES QUE SÍ VAN A LLEGAR
                    if (targetTransitions.remove(targetTransitions.size() - 1).read.equals("F")) {
                        for (PDATransition p : targetTransitions) {
                            current = p.run(tape, stack);
                            myTransitions.add(p);//AGREGO A MI LISTA
                        }
                        encontrado = true;
                        break;
                    }
                    if(pdaTransitions.isEmpty()&&!encontrado){
                        myTransitions.add(new PDATransition("N"));
                        return myTransitions;
                    }
                }
            }
        }
        if (tape.isEmpty() && stack.isEmpty()) {
            myTransitions.add(new PDATransition("F"));
        } else {
            myTransitions.add(new PDATransition("N"));
        }
        return myTransitions;
    }
}
