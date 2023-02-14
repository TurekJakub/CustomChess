
public class OwOExecutionist {
    public static void main(String[] args) {
        Board board = new Board(8, 8);

        String[] sarr = {"1/2", "om1.(1/3)"};
        Piece p1 = new Piece(sarr);
        p1.mark = "N";
        board.place(p1, 2, 2);

        Piece p2 = new Piece(sarr);
        p2.mark = "P";
        p2.color = "\033[0;36m";
        board.place(p2, 1, 5);

        board.print();

        MoveInterpreter mi = new MoveInterpreter(board);
        System.out.println(mi.getValidMoves(p1.moves[1], 0, 0, p1).toString());;
    }
}
