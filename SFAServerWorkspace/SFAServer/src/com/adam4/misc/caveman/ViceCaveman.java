package com.adam4.misc.caveman;

// adapted from Manu's submission: http://codegolf.stackexchange.com/questions/34968/caveman-duels-or-me-poke-you-with-sharp-stick/34983#34983
// included here to test against

public class ViceCaveman {
    
    public static void main(String[] args)
    {
        System.out.println(run(args));
    }

    static String run(String[] args) {
        if (args.length == 0 || !args[0].contains(",")) {
            return("S");
        }
        String[] history = args[0].split(",");
        int mySharpness = getSharpness(history[0]);
        int enemySharpness = getSharpness(history[1]);

        // enough sharpness to strike until end of game
        if (100 - history[0].length() <= mySharpness) {
            return("P");
        }

        // sharpen while secure
        if (enemySharpness == 0) {
            return("S");
        }

        // enemy blocks the whole time
        if (isBlocker(history[1])) {
            return("S");
        }

        // TAKE HIM OUT!
        if (enemySharpness == 4 || mySharpness >= 5) {            
            return("P");
        }

        // enemy sharpens the whole time => sharpen to strike on next turn
        if (isSharpener(history[1])) {
            return("S");
        }

        return("B");
    }

    private static int getSharpness(String history) {
        int sharpness = 0;
        for (char move : history.toCharArray()) {
            if (move == 'S') {
                sharpness++;
            } else if ((move == 'P' && sharpness > 0) || move == '^') {
                sharpness--;
            }
        }
        return sharpness;
    }

    private static boolean isBlocker(String history) {
        if (history.length() < 10) {
            return false;
        }
        for (int i = history.length() - 1; i > history.length() - 10; i--) {
            if (history.charAt(i) != 'B') {
                return false;
            }
        }
        return true;
    }

    private static boolean isSharpener(String history) {
        if (history.length() < 3) {
            return false;
        }
        for (int i = history.length() - 1; i > history.length() - 3; i--) {
            if (history.charAt(i) != 'S') {
                return false;
            }
        }
        return true;
    }
}
