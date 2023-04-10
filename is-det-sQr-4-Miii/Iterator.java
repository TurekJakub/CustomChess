import java.util.Arrays;
import java.util.HashSet;


public class Iterator {
    MoveInterpreter mi;
    private int x0;
    private int y0;

    Piece p;
    String s;
    char c = '*', atc;

    int start = 1, end = 1;
    int stepA = 1, stepB = 1;
    
    boolean leap = false, hop = false;
    boolean mustCapture = false, mustNotCapture = false;
    HashSet<int[]> dirs = new HashSet<>();
    HashSet<int[]> dir2 = new HashSet<>();

    public HashSet<int[]> gimmeSet(){
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if(i == 0 && j == 0) continue;
                addarr1(i * stepA, j * stepB);
            }
        }
        if(c == '/'){// augment step ab -> ab, ba
            for (int[] is : dirs) {
                addarr(new int[] {is[1], is[0]});
            }
            dirs.addAll(dir2);
        }
        //^^^^ primary dirs ^^^^

        boolean orForward = false;
        boolean samotny = false;
        if(c == 'n' || atc == '°') samotny = true;

        int i = 1;
        System.out.println("prim dirs");
        for (int[] js : dirs) {
            System.out.print(Arrays.toString(js));
        }
        System.out.println();
        System.out.println(c + " " + atc);

        while(i <= 2){
            if(i == 1) {orForward = false;}//andForward, restrikce
            if(i == 2) {orForward = true;} //prida forward

            dir2.clear();
            //dir2.addAll(dirs);

            System.out.println();
            switch(i == 1 ? atc : c){ //prohozene poradi characteru, cause <> musi pridat az nakonec
                case '*': // tak to je už
                    dir2.addAll(dirs);
                    break;

                case '+':
                    for (int[] is : dirs) {
                        if(is[0] == 0 || is[1] == 0){addarr(is);}//keep
                        //else dir2.remove(new int[] {is[0], is[1]});
                    }
                    break;
    
                case 'X':
                    for (int[] is : dirs) {
                        if(is[0] == is[1]){addarr(is);}//keep
                        if(is[0] == -is[1]){addarr(is);}//keep
                        //else dir2.remove(is);
                    }
                    break;

                case '/':
                    for (int[] is : dirs) {
                        boolean keep = true;
                        if(is[0] == 0 && stepA != 0){keep = false;}
                        if(is[1] == 0 && stepB != 0){keep = false;}
                        if(keep){ addarr(is);}
                        //else dir2.remove(is);
                    }
                    break;
                
                case '=':
                    for (int[] is : dirs) {
                        if(is[0] == 0){addarr(is);}//keep
                        //else dir2.remove(is);
                    }
                    break;

                case '<':
                    if(samotny){
                        addarr(new int[] {0, -1});
                        break;
                    }
                    if(orForward){
                        if(atc != '°') dir2.addAll(dirs);
                        addarr(new int[] {0, -stepA});
                        if(stepA != stepB) addarr(new int[] {0, -stepB});
                    }
                    else{for (int[] is : dirs) {
                        if(is[1] < 0){addarr(is);}//keep
                        //else dir2.remove(is);
                    }}
                    break;

                case '>':
                    if(samotny){
                        addarr(new int[] {0, 1});
                        break;
                    }
                    if(orForward){
                        if(atc != '°') dir2.addAll(dirs);
                        addarr(new int[] {0, stepA});
                        if(stepA != stepB) addarr(new int[] {0, stepB});
                    }
                    else{for (int[] is : dirs) {
                        if(is[1] > 0){addarr(is);}//keep
                        //else dir2.remove(is);
                    }}
                    break;

                default:
                    dir2.addAll(dirs);
                    System.out.println("neni 2hy znak");
            }
            dirs.clear();
            dirs.addAll(dir2);
            i++;
        }
        /*
        dir2.clear();// no duplicates
        dirs.has
        dirs = (dirs);
        */
        /*
        for (int[] is : dirs) {
            if(!dir2.contains(is)){
                addarr(is);
                System.out.println(Arrays.toString(is));
            }
        }
        dirs.clear();
        dirs.addAll(dir2);
        */

        //iteration
        HashSet<int[]> set = new HashSet<>();
        System.out.println(start);
        System.out.println(end);
        System.out.println("---");
        for (int[] ita: dirs) {
            int max = Math.max(Math.abs(ita[0]), Math.abs(ita[1]));
            if(max == 0){set.add(new int[] {0,0}); continue;}

            int x1 = x0;
            int y1 = y0;
            int k = start;

            for (int j = start; j <= end * max; j++) {

                if(j % max != 0){
                    if(leap) {continue;}
                    else {continue;}// check for a piece necessary [2, 0] -> break
                }

                //x1 = j;
                //int y1 = j * ita[1] / ita[0];

                int x2 = x0 + k * ita[0];
                int y2 = y0 + k * ita[1];

                if(x2 < 0 || y2 < 0 || x2 >= mi.board.width || y2 >= mi.board.height) break; //add board attr

                if (
                    mi.board.gimme(x2, y2) != null 
                    && (x2!=p.x && y2!=p.y) 
                    && mi.board.gimme(x2, y2).team == p.team);
                else set.add(new int[] {x2, y2});

                k++;
                x1 = x2; y1 = y2;//posun akt policka o 1 krok
                //System.out.println(x2 + y2);
            }

            
        }
        System.out.println("dirs:");
        for (int[] js : dirs) {
            System.out.print(Arrays.toString(js));
        }
        System.out.println();
        System.out.println("set:");
        for (int[] js : set) {
            System.out.print(Arrays.toString(js));
        }

        return set;
        //return null;

    
    }
    public Iterator(Piece p, String s, char c, char atc, MoveInterpreter mi, int x, int y) {
        this.mi = mi;
        this.p = p;
        this.s = s;
        this.c = c;
        this.atc = atc;
        this.start = mi.start;
        this.end = mi.end;
        this.stepA = mi.stepA;
        this.stepB = mi.stepB;
        this.leap = mi.leap;
        this.hop = mi.hop;
        this.mustCapture = mi.mustCapture;
        this.mustNotCapture = mi.mustNotCapture;
        this.x0 = x;
        this.y0 = y;
    }

    private void addarr1(int a, int b){
        int[] t = {a,b};
        dirs.add(t);
    }

    private void addarr(int[] it){
        /* and there it is, the supposed pro of a hashSet gone,
        unfortunatelly hashCode() of and int[] cant be overriden */
        boolean add = true;
        for (int[] is : dir2) {
            if(is[0] == it[0] && is[1] == it[1])
            {add = false; break;}
        }
        if(add) dir2.add(it);
    }

}
