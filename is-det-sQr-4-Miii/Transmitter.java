import java.util.HashMap;
import java.util.HashSet;

public class Transmitter {
    Board board;

    // XXX
    public HashMap<String, int[]> getPieces(){
        HashMap<String, int[]> hm = new HashMap<>();
        for (Piece p: board.piecist) {
            hm.put(p.name, new int[] {p.x,p.y});
        }
        return hm;
    }

    // XX
    public HashMap<int[], HashSet<String>> getPosition2MoveNames(){
        HashMap<int[], HashSet<String>> hm = new HashMap<>();
        for (Piece p : board.piecist) {
            HashSet<String> names = new HashSet<>();
            for (String s : p.moveNames) {
                names.add(s);
            }
            hm.put(new int[] {p.x,p.y}, names);
        }
        return hm;
    }

    // X
    public HashMap<String, HashMap<String, HashSet<int[]>>> getMNaP2RandomAccessPathTree(){
        HashMap<String, HashMap<String, HashSet<int[]>>> hm = new HashMap<>();
        for (Piece p : board.piecist) {
            for (String s : p.moveNames) {
                MoveInterpreter mi = new MoveInterpreter(null);
                mi.getValidMoves(s, p.x, p.y, p);
                //HashMap<String, HashSet<int[]>> hm2 = mi.stm;
                hm.put(MoveInterpreter.key(p.x, p.y, s), mi.stm);
            }
        }
        return hm;
    }

    public Transmitter(Board board) {
        this.board = board;
    }
    
}
