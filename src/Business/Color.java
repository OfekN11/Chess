package Business;

import java.util.*;

public enum Color { Black,White, None;
 public static List<Color> getOpponent(Color color, int numberOfPlayers){
     if (numberOfPlayers == 2 && color == White || color == Black){
         List<Color> colors = new ArrayList<>(1);
         if (color == White)
             colors.add(Black);
         else
             colors.add(White);

         return colors;
     }
     throw new RuntimeException("no such table");
 }
}
