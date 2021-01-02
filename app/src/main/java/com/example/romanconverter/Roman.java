package com.example.romanconverter;

/**
 * Description : A program with methods that can convert between roman numerals and the base 10 system
 * @author Namdar S
 */
public class Roman {
    private static String[] symbols =  {"M", "D", "C", "L", "X", "V", "I"};
    private static int[] values = {1000, 500, 100, 50, 10, 5, 1};

    public Roman() {

    }

    /**
     * Description : A method to convert integers to roman numerals
     * @param n an integer to be converted to roman numerals
     * @return integer n in roman numerals
     */
    public static String convertToString(int n) {
        String result = "";

        for (int i = 0; i < values.length; i++) {
            int firstDigit = Integer.parseInt(Integer.toString(n).substring(0, 1)); //get first digit of n

            //subtraction is applied only when number begins with 4 or 9, check for that first
            if (firstDigit == 9 && (n - values[i] > 0)) {
                result += (symbols[i + 1] + symbols[i - 1]); //add the next symbol and previous symbol, e.g "IX" to result
                n -= (values[i - 1] - values[i + 1]); //subtract that value from n

            } else if (firstDigit == 4 && n < 1000 && (n - values[i] > 0)) { //check if current value can be subtracted from n
                result += (symbols[i] + symbols[i - 1]); //otherwise index at i - 1 would give an out of bounds error
                n -= (values[i - 1] - values[i]);

            }
            //if no subtraction is necessary
            while (n >= values[i]) {
                result += (symbols[i]);
                n -= values[i]; //value that was added to result is subtracted from n in all conditionals
            }
        }

        return result;
    }

    /**
     * Description : a method to convert a string of roman numerals into integers
     * @param s a string of roman numerals to be converted to integers
     * @return string s in integers
     */
    public static int convertToInt(String s) {
        int[] numbers = new int[s.length() + 1]; //numbers.length = s.length + 1 to work around out of bounds error
        int result = 0;

        for (int i = 0; i < s.length(); i++) {
            String cur = String.valueOf(s.charAt(i));
            for (int j = 0; j < symbols.length; j++) { //nested loop that checks if a symbol is present in symbols array
                if (cur.equals(symbols[j])) { //if there is a match
                    numbers[i] = values[j];   // then the value of that symbol is added to the local numbers array
                }
            }
        }

        for (int i = 0; i < numbers.length - 1; i++) {
            if (numbers[i] >= numbers [i + 1]) { //if current number is >= next number, it is added to result
                result += numbers[i];
            } else { //otherwise the next number is bigger
                result += (numbers[i + 1] - numbers[i]); //in which case next number - current number is added to result
                i++; //i is incremented so that the next number is skipped since it has already been accounted for
            }

        }

        if (convertToString(result).equals(s)) {
            return result;
        }
        return 0;
    }
}

