import java.util.ArrayList;
import java.util.HashMap;

public class Piece {
    String name;
    String mark = "?";
    String color = "\033[0;35m";

    ArrayList<Tag> tags;
    HashMap<String, Integer> counters = new HashMap<>();

    String[] moves;
    String[] conditions;
    String[] execute;
    String[] moveNames;

    public String print() {
        return color + mark + "\033[0m";
    }


    public Piece(/*String name, String mark, String color, */ String[] moves /*String[] conditions, String[] execute*/) {
        /*this.name = name;
        this.mark = mark;
        this.color = color;
        this.tags = tags;*/
        
        
        counters.put("moved", 0);
        this.moves = moves;
        /*
        this.conditions = conditions;
        this.execute = execute;
        */
    }
    
    
}
