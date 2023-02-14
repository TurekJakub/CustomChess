import java.util.ArrayList;
import java.util.HashSet;

public class MoveInterpreter {
    Board board;

    public HashSet<int[]> getValidMoves(String s, int x, int y, Piece p){
        HashSet<int[]> validMoves = new HashSet<>();
        int i = 0;
        System.out.println("gVM");

        if(ch(s, i) == 'i' && p != null){
            if(p.counters.get("moved").intValue() > 0) return null;
            i++;
        }

        boolean mustCapture;
        boolean mustNotCapture;
        if(ch(s, i) == 'c') {mustCapture = true; i++;}
        if(ch(s, i) == 'o') {mustNotCapture = true; i++;}

        int start = 0; // proc 0
        int end = 0;
        boolean lengthFound = false;

        
        boolean continues = true;
        while(continues){
            continues = false;
            if(i == s.length()) return validMoves; //>=
            //m1 , n0X n0() , 1/2
            //minibinboard

            if(ch(s, i) == 'm'){ // odkaz na jiné move figury
                String[] sarr = s.split("m", 2);
                String[] sarr2 = sarr[1].split("[^0-9]", 2);
                String[] sarr3 = sarr[1].split(sarr2[0], 2);
                try {//           vse pred m,             urcite remoat move,                                              rozdelovaci znak po cisle indexu + zbytek
                    String newS = sarr[0].concat("(").concat(p.moves[Integer.parseInt(sarr2[0]) - 1]).concat(")").concat(sarr3[1]);
                    System.out.println(newS);
                    return this.getValidMoves(newS, x, y, p);
                } catch (Exception e) {
                    System.out.println("ajaj tady pak musis vratit prazdny seznam");
                }
            }
            
            if(ch(s, i) == 'n'|| (s.substring(i, i+1)).matches("[0-9]")){

                if(ch(s, i) == 'n'){
                    start = 1;
                    end = Integer.MAX_VALUE;
                    if (ch(s, i-1) == '0') start = 0;
                    i++;
                    lengthFound = true;
                }
                else{
                    int at;
                    String[] sarr = s.substring(i).split("[^0-9]", 2);
                    char c = s.charAt(i + (at = sarr[0].length()));
                    String[] sarr2 = s.substring(i + ++at).split("[^0-9]", 2);

                    at += sarr2[0].length();
                    boolean notActually = false;

                    switch(c){
                        case 'n':
                        continues = true;
                        i+= sarr[0].length();
                        notActually = true;
                        break;

                        case '/':
                        System.out.println("/");
                        if(!lengthFound){start = 1; end = 1;}
                        //jump(Integer.parseInt(sarr[0]), Integer.parseInt(sarr2[0]));
                        i += at;
                        break;

                        case '-':
                        start = Integer.parseInt(sarr[0]);
                        end = start;
                        if(sarr2[0] != "") end = Integer.parseInt(sarr2[0]);
                        i += at;
                        break;

                        default:
                        //iterate(Integer.parseInt(sarr[0]), c);
                        i += at;
                    }

                    if(!notActually) lengthFound = true;

                }

            }

            if(ch(s, i) == '(' || ch(s, i) ==')'){i++; continues = true;}

            if(ch(s, i) == '.'){
                HashSet hs = new HashSet<int[]>(validMoves.size()*8);
                for (int[] js : validMoves) {
                    hs.addAll(this.getValidMoves(s, js[0], js[1], p));
                }
                return hs;

            }

            System.out.println("i: " + i);
        }
        return validMoves;
    }

    private static char ch(String s, int i){
        try {
            return s.charAt(i);
        } catch (IndexOutOfBoundsException e) {
            return '°';
        }
    }

    private static HashSet<int[]> iterator(int start, int end, int stepX, int stepY ){return null;}

    public MoveInterpreter (Board board){
        this.board = board;
    }
}
