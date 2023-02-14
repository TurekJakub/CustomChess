import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    int width;
    int height;
    Square[][] squarr;
    boolean[][][] binboard;
    ArrayList<Piece> piecist = new ArrayList<>();
    HashMap<Piece, ArrayList<int[]>> validMoves = new HashMap<>();
    

    public Board(int width, int height /*Rules rules, layout */) {
        this.width = width;
        this.height = height;
        this.squarr = new Square[height][width]; 

        fillWithSquares();
        binboard = new boolean[height][width][3];
    }

    public Piece gimme(int x, int y){
        return squarr[x][y].occupant;
    }

    public void place(Piece p, int x, int y){
        squarr[y][x].occupant = p;
        piecist.add(p);
        validMoves.put(p, null);
    }

    public void print(){
        String charBoard = "";
        for (int i = height - 1; i >= 0; i--) {
            for (int j = 0; j < width; j++) {
                if(squarr[i][j].occupant != null){
                    charBoard += squarr[i][j].occupant.print();
                }
                else{
                    charBoard += (i + j) % 2 == 0 ? (char) '#' : (char) 'O';
                }
                charBoard += " ";
            }
            charBoard += "\n";
        }
        System.out.println(charBoard);
    }

    private void fillWithSquares(){
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                squarr[i][j] = new Square();
            }
        }
    }

}
