package com.govodata.paul.parrottalk;

import org.apache.commons.codec.language.Metaphone;

import java.util.HashMap;

class Controller {

    // Array of all the commands supported.
    // Some are alternate words for the same command.
    // See convertCommand method below.
    private static final String[] words = {
            "takeoff",
            "land",
            "touchdown",
            "emergency",
            "zero",
            "set",
            "return",
            "comeback",
            "hover",
            "stop",
            "stay",
            "hold",
            "forward",
            "forwards",
            "front",
            "straight",
            "ahead",
            "head",
            "backward",
            "backwards",
            "back",
            "behind",
            "tail",
            "reverse",
            "right",
            "left",
            "counterclockwise",
            "clockwise",
            "up",
            "upward",
            "upwards",
            "top",
            "down",
            "downward",
            "downwards",
            "bottom",
            "altitude",
            "yaw",
            "twist",
            "rotate"
    };

    private Metaphone metaphone = new Metaphone();

    // Table containing all the metaphones of the command words.
    // <metaphone, word>
    private HashMap<String, String> metaphoneTable = new HashMap<>();

    Controller() {
        // Uses metaphone algorithm to find similar utterances.
        for (String w : words) {
            this.metaphoneTable.put(metaphone.encode(w), w);
        }
    }

    // Returns command in the format:
    // "command" (no digit), "command \d.\d" (command + float digit)
    String parseString(final String s) {
        String[] sArray = s.split(" ");
        if (sArray.length == 0) {
            throw new IllegalArgumentException("Empty string.");
        }
        else if (sArray.length == 1) {
            if (metaphoneTable.containsKey(metaphone.encode(sArray[0]))) {
                return convertCommand(metaphoneTable.get(metaphone.encode(sArray[0])));
            }
            else if (sArray[0].equals("0")) {
                return "zero";
            }
            else {
                throw new IllegalArgumentException("Unable to parse \"" + s + "\"");
            }
        }
        else if (sArray.length == 2) {
           // "take off"
            if (metaphone.isMetaphoneEqual("take", sArray[0]) && metaphone.isMetaphoneEqual("off", sArray[1])) {
               return "takeoff";
            }
            // "touch down"
            else if (metaphone.isMetaphoneEqual("touch", sArray[0]) && metaphone.isMetaphoneEqual("down", sArray[1])) {
                return "land";
            }
            // "come back"
            else if (metaphone.isMetaphoneEqual("come", sArray[0]) && metaphone.isMetaphoneEqual("back", sArray[1])) {
                return "return";
            }
        }

        String commandString = null;
        double digit = -1;
        boolean feet = false;
        for (String str : sArray) {
            if (metaphoneTable.containsKey(metaphone.encode(str))) {
                commandString = convertCommand(metaphoneTable.get(metaphone.encode(str)));
                // These commands are singular and do not have a digit.
                switch (commandString) {
                    case "takeoff":
                    case "land":
                    case "zero":
                    case "return":
                        return commandString;
                    default:
                        break;
                }
            }
            // Find digits with regex.
            else if (str.matches("(\\d+(\\.\\d+)?|\\.\\d+)")) {
                digit = Double.parseDouble(str);
            }
            // Check for feet/foot. Default unit is meters.
            else if (metaphone.isMetaphoneEqual("feet", str) || metaphone.isMetaphoneEqual("foot", str)) {
                feet = true;
            }
            else if (metaphone.isMetaphoneEqual("one", str)) {
                digit = 1;
            }
            else if (metaphone.isMetaphoneEqual("two", str)) {
                digit = 2;
            }
            else if (metaphone.isMetaphoneEqual("three", str)) {
                digit = 3;
            }
            else if (metaphone.isMetaphoneEqual("four", str)) {
                digit = 4;
            }
            else if (metaphone.isMetaphoneEqual("five", str)) {
                digit = 5;
            }
            else if (metaphone.isMetaphoneEqual("six", str)) {
                digit = 6;
            }
            else if (metaphone.isMetaphoneEqual("seven", str)) {
                digit = 7;
            }
            else if (metaphone.isMetaphoneEqual("eight", str)) {
                digit = 8;
            }
            else if (metaphone.isMetaphoneEqual("nine", str)) {
                digit = 9;
            }
            else if (metaphone.isMetaphoneEqual("ten", str)) {
                digit = 10;
            }
        }

        if (commandString == null) {
            throw new IllegalArgumentException("Unable to parse \"" + s + "\"");
        }
        else if (digit == -1) {
            return commandString;
        }
        // Convert from feet to meters.
        else if (feet) {
            return commandString + " " + feetToMeters(digit);
        }

        return commandString + " " + digit;
    }

    // Conversion of alias commands.
    private static String convertCommand(final String s) {
        switch (s) {
            case "land":
            case "touchdown":
            case "emergency":
                return "land";
            case "zero":
            case "set":
                return "zero";
            case "return":
            case "comeback":
                return "return";
            case "hover":
            case "stop":
            case "stay":
            case "hold":
                return "hover";
            case "forward":
            case "forwards":
            case "front":
            case "straight":
            case "ahead":
            case "head":
                return "forward";
            case "backward":
            case "backwards":
            case "back":
            case "behind":
            case "tail":
            case "reverse":
                return "backward";
            case "up":
            case "upward":
            case "upwards":
            case "top":
                return "up";
            case "down":
            case "downward":
            case "downwards":
            case "bottom":
                return "down";
            case "yaw":
            case "twist":
            case "rotate":
                return "yaw";
            default:
                return s;
        }
    }

    private static double feetToMeters(final double feet) { return feet / 3.2808; }
}
