public class ConditionCommandInterpreter{

    public Entity satisfy(String s, Entity[] ents){
        Entity forReturn;
        Entity currEntity;

        String orderType; //"Command"
        String state = "Start"; //"area selection"
        
        int i = 0;
        while(i < s.length()){

            if(state == "Start"){
                char ch = s.charAt(i);
                if(ch == 'X') orderType = "Command";
                else if(ch == 'E') orderType = "Any";
                else if(ch == 'V') orderType = "All";

                state = "Search for Entity type";
                i++;
            }


        }
    }
}