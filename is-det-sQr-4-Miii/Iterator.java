import java.util.HashSet;


public class Iterator {
    Piece p;
    String s;
    char c = '*', atc;

    int start = 1, end = 1;
    int stepA = 1, stepB = 1;
    
    boolean leap = false, hop = false;
    boolean mustCapture = false, mustNotCapture = false;
    HashSet<int[]> dirs = new HashSet<>();

    public HashSet<int[]> gimmeSet(){
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if(i == 0 && j == 0) continue;
                addarr(i * stepA, j * stepB);
            }
        }

        boolean orForward = false;
        int i = 1;

        while(i <= 2){
            if(i == 1) {orForward = false;}//andForward, restrikce
            if(i == 2) {orForward = true;} //prida forward
            
            switch(i == 1 ? atc : c){ //prohozene poradi characteru, cause <> musi pridat az nakonec
                case '*': // tak to je už

                case '+':
                    for (int[] is : dirs) {
                        if(is[0] == 0 || is[1] == 0){}//keep
                        else dirs.remove(is);
                    }
    
                case 'X':
                    for (int[] is : dirs) {
                        if(is[0] == is[1]){}//keep
                        else dirs.remove(is);
                    }
                    break;
                
                case '=':
                    for (int[] is : dirs) {
                        if(is[0] == 0){}//keep
                        else dirs.remove(is);
                    }
                    break;

                case '<':
                    if(orForward){addarr(0, -stepA); addarr(0, -stepB);;}
                    else{for (int[] is : dirs) {
                        if(is[1] < 0){}//keep
                        else dirs.remove(is);
                    }}
                    break;

                case '>':
                    if(orForward){addarr(0, stepA); addarr(0, stepB);;}
                    else{for (int[] is : dirs) {
                        if(is[1] > 0){}//keep
                        else dirs.remove(is);
                    }}
                    break;
            }
            i++;
        }

        return dirs;
        //return null;

    
    }
    public Iterator(Piece p, String s, char c, char atc, MoveInterpreter mi) {
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
    }

    private void addarr(int a, int b){
        int[] t = {a,b};
        dirs.add(t);
    }
}
