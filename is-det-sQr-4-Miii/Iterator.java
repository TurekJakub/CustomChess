import java.util.Set;
import java.util.HashSet;


public class Iterator {
    String s;
    int start = 1, end = 1;
    int stepA = 1, stepB = 1;
    char c = '*';
    Set<int[]> dirs;


    public HashSet<int[]> asdf(Piece p, char atc, boolean leap){
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
        
        
        
        return null;
    }


    public Iterator(String s, int start, int end, int stepA, int stepB, char c) {
        this.s = s;
        this.start = start;
        this.end = end;
        this.stepA = stepA;
        this.stepB = stepB;
        this.c = c;
    }

    public Iterator(String s, int start, int end, char c) {
        this.s = s;
        this.start = start;
        this.end = end;
        this.stepA = 1;
        this.stepB = 1;
        this.c = c;
    }

    private void addarr(int a, int b){
        int[] t = new int[]{a,b};
        dirs.add(t);
    }
}