import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CFG {
    public static void main(String[]args){
        String cadena = leer();
        ArrayList<String> nonterminal = new ArrayList<String>();
        String snonterminal = cadena.split("\n")[0];
        for(String s:snonterminal.split(",")){
            nonterminal.add(s);
        }
        ArrayList<String> terminal = new ArrayList<String>();
        String sterminal = cadena.split("\n")[1];
        for(String s:sterminal.split(",")){
            terminal.add(s);
        }

        String initial=cadena.split("\n")[2];
        String example=cadena.split("\n")[3];
        ArrayList<CFGTransition> lista = new ArrayList<CFGTransition>();
        for(int i=4;i<cadena.split("\n").length;i++){
            lista.add(new CFGTransition(cadena.split("\n")[i]));
        }

        PDA pda = new PDA(initial, lista, nonterminal,terminal);

        if(pda.run(example)){
            System.out.println("Accepted");
        }else{
            System.out.println("Not accepted");
        }


    }

    public static String leer() {
        BufferedReader inputStream = null;
        String res="";
        try
        {

            inputStream = new BufferedReader(new FileReader("grammar.txt"));

            String l="";
            while ((l = inputStream.readLine()) != null)
            {
                res+=l;
                res+="\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            if( inputStream != null )
            {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return res;

    }
}
