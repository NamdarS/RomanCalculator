package com.example.romanconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
    //decimal buttons and button array
    Button decimal0, decimal1, decimal2, decimal3, decimal4,
           decimal5, decimal6, decimal7, decimal8, decimal9;
    Button[] decimalButtons;

    //roman buttons and button and values arrays
    Button roman0, roman1, roman2, roman3, roman4,
           roman5, roman6;
    Button[] romanButtons;
    String[] romanValues;

    //numbers and operations
    String numberEntered = "";
    String firstNumberEntered = ""; //stores numberEntered when doing operations
    String operation = ""; //keep track of what operation is being performed using the view's tag
    String buttonsDisplayed = ""; //keep track of what buttons are displayed using the view's tag

    //keeping track of operations and number system
    boolean calculation = false; //set to true when an operation button is pressed
    boolean readyToCalculate = false; //set to true when second number has been entered
    boolean calculationDone = false; //set to true when calculation was completed
    boolean romanButtonsOn = false; //keep track of which buttons are on
    boolean operationSelected = false; //set to true when chaining operations
    boolean decimalDisplay = true; //keep track of whether display is roman or decimal

    //display and data storage
    TextView display;
    TextView answerDisplay;
    Hashtable<Integer, Integer> decimalButtonValues;
    Hashtable<Integer, String> romanButtonValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize
        display = findViewById(R.id.textView);
        answerDisplay = findViewById(R.id.answerTextView);
        decimalButtonValues = new Hashtable<>();
        romanButtonValues = new Hashtable<>();

        //decimal buttons array
        decimalButtons = new Button[]{decimal0, decimal1, decimal2, decimal3, decimal4,
                            decimal4, decimal5, decimal6, decimal7, decimal8, decimal9};

        //find decimal buttons using ids and add button and value to hashtable
        for (int i = 0; i < decimalButtons.length; i++) {
            int id = getResources().getIdentifier("button" + i, "id", getPackageName());
            decimalButtons[i] = findViewById(id);
            decimalButtonValues.put(id, i);
        }

        //roman buttons and values arrays
        romanButtons = new Button[]{roman0, roman1, roman2, roman3, roman4, roman5, roman6};
        romanValues = new String[]{"I", "V", "X", "L", "C", "D", "M"};

        //find roman buttons using ids and add button and value to hashtable
        for (int i = 0; i < romanButtons.length; i++) {
            int id = getResources().getIdentifier("roman" + i, "id", getPackageName());
            romanButtons[i] = findViewById(id);
            romanButtonValues.put(id, romanValues[i]);
        }
    }

    /**
     * Event handling for all decimal buttons
     * @param view the decimal button that was pressed
     */
    public void numberButtonClick(View view) {
        buttonAnimation(view);

        if (!romanButtonsOn) {
            //prepare for decimal use
            if (!decimalDisplay) {
                numberEntered = "";
                display.setText("");
                decimalDisplay = true;
            }

            //prepare for decimal numbers to be entered
            checkTypingConditions();
            //button that was pressed
            Button curButton = (Button) view;
            //get value of curButton that was pressed from hashtable
            int curValue = decimalButtonValues.get(curButton.getId());
            numberEntered += String.valueOf(curValue);

            try {
                checkLimit();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                numberEntered = numberEntered.substring(0, numberEntered.length() - 1);
                return;
            }

            display.append(String.valueOf(curValue));
        }
    }

    /**
     * Event handling for all roman numeral buttons
     * @param view the roman rnumeral button that was pressed
     */
    public void romanButtonClick(View view) {
        buttonAnimation(view);

        if (romanButtonsOn) {
            //prepare for roman use
            if (decimalDisplay) {
                numberEntered = "";
                display.setText("");
                decimalDisplay = false;
            }

            //prepare for roman numerals to be entered
            checkTypingConditions();

            //button that was pressed
            Button curButton = (Button) view;
            //get value of curButton from hashtable
            String curValue = romanButtonValues.get(curButton.getId());
            numberEntered += curValue;

            try {
                checkLimit();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                numberEntered = numberEntered.substring(0, numberEntered.length() - 1);
                return;
            }

            //disable or enable any buttons that need to be disabled or enabled
            romanRules();
            display.append(curValue);
        }
    }

    /**
     * Backspace functionality
     * @param view the backspace button
     */
    public void deleteClick(View view) {
        buttonAnimation(view);

        if (display.length() > 0) {
            numberEntered = numberEntered.substring(0, numberEntered.length() - 1);
            display.setText(numberEntered);
            //enable all buttons
            enableRomanButtons();
            //apply roman numeral rules to the new number
            romanRules();
        }
    }

    /**
     * Arithmetic functionality and keeping track of
     * which operation is underway using the tag
     * attribute of the view
     * @param view the operation button that was pressed
     */
    public void operationClick(View view) {
        buttonAnimation(view);
        String message = "";

        if (numberEntered.length() > 0) {
            message =  "Convert back to do operations";
            //operations only allowed when display type matches buttons ie not converted
            if (decimalDisplay != romanButtonsOn) {
                if (!calculation && !readyToCalculate ||calculationDone) {//one operation at a time
                    operation = (String) view.getTag(); //"add", "subtract", "multiply" or "divide"
                    calculation = true;
                    operationSelected = true;
                    if (operation.equals("divide")) {
                        message = "Quotient will be rounded";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    enableRomanButtons(); //enable all buttons for second number to be entered
                    return;
                } else {
                    message = "One operation at a time";
                }
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Perform calculations and display answer
     * @param view the equals button
     */
    public void calculationClick(View view) {
        buttonAnimation(view);

        if (readyToCalculate) {
            int answer;
            int firstNumber;
            int secondNumber;


            if (romanButtonsOn) {
                firstNumber = Roman.convertToInt(firstNumberEntered);
                secondNumber = Roman.convertToInt(numberEntered);
                if (firstNumber == 0 || secondNumber == 0) {
                    String message = "Invalid Roman numeral entered";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    resetValues();
                    return;
                }
            } else {
                firstNumber = Integer.parseInt(firstNumberEntered);
                secondNumber = Integer.parseInt(numberEntered);
            }

            //find out what operation user selected and perform it
            if (operation.equals("add")) {
                answer = firstNumber + secondNumber;
            } else if (operation.equals("subtract")) {
                answer = firstNumber - secondNumber;
            } else if (operation.equals("multiply")) {
                answer = firstNumber * secondNumber;
            } else {
                answer = firstNumber / secondNumber;
            }

            try {
                checkLimit(answer);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                resetValues();
                return;
            }

            if (romanButtonsOn) {
                numberEntered = Roman.convertToString(answer);
            } else {
                numberEntered = String.valueOf(answer);
            }

            //update display, set strings & booleans & enable buttons after successful calculation
            answerDisplay.setText(numberEntered);
            display.setText("");
            readyToCalculate = false;
            calculationDone = true;
            operation = "";
            firstNumberEntered = "";
            enableRomanButtons();
        }
    }

    /**
     * Convert from decimal to roman and vice versa
     * @param view the convert button
     */
    public void convertValueClick(View view) {
        buttonAnimation(view);
        //conversion not allowed during a calculation
        if (!calculation || !readyToCalculate) {
            if (numberEntered.length() > 0) {
                if (decimalDisplay) {
                    int value = Integer.parseInt(numberEntered);
                    numberEntered = Roman.convertToString(value);
                    display.setText(numberEntered);
                    decimalDisplay = false;
                } else {
                    if (String.valueOf(Roman.convertToInt(numberEntered)).equals("0")) {
                        String message = "Invalid Roman numeral entered";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    numberEntered = String.valueOf(Roman.convertToInt(numberEntered));
                    display.setText(numberEntered);
                    decimalDisplay = true;
                }
            }
        }
    }

    /**
     * Change buttons from decimal to roman and vice versa
     * @param view either the "DEC button or the "ROM" button
     */
    public void changeButtonsClick(View view) {
        buttonAnimation(view);
        answerDisplay.setText("");
        buttonsDisplayed = (String) view.getTag();

        //check if buttons need to be changed
        if (buttonsDisplayed.equals("roman") && !romanButtonsOn) {
            toggleButtons(romanButtons, decimalButtons);
            romanButtonsOn = true;
            decimalDisplay = false;
        } else if (buttonsDisplayed.equals("decimal") && romanButtonsOn) {
            toggleButtons(decimalButtons, romanButtons);
            romanButtonsOn = false;
            decimalDisplay = true;
        }
        //reset all values to initial state when changing buttons
        resetValues();
    }

    /**
     * Clear displays and reset values
     * @param view the clear button, labelled "C"
     */
    public void clearClick(View view) {
        buttonAnimation(view);
        resetValues();
        if (romanButtonsOn) {
            enableRomanButtons();
        }
    }

    /**
     * Switch to help menu where roman numeral
     * definitions are available to user
     * @param view the question mark button
     */
    public void switchActivityClick(View view) {
        buttonAnimation(view);
        Intent intent = new Intent(MainActivity.this, RomanDefinitionsActivity.class);
        startActivity(intent);
    }

    /**
     * Perform an animation on a button and send
     * haptic feedback to the user
     * @param view the button to which the animation will be applied
     */
    public void buttonAnimation(View view) {
        //make button smaller than full size again
        float pivotX = (float) view.getWidth() / 2;
        float pivotY = (float) view.getHeight() / 2;
        ScaleAnimation animation =
                new ScaleAnimation(0.9f, 1f, 0.9f, 1f, pivotX, pivotY);
        animation.setDuration(150);
        view.startAnimation(animation);
        view.performHapticFeedback(1);
    }

    /**
     * Toggle button visibility when changing
     * from decimal to roman buttons or vice versa
     * @param on array of buttons that will be made visible
     * @param off array of buttons that will be made invisible
     */
    public void toggleButtons (Button[] on, Button[] off) {
        //loop through button array and toggle visibility
        for (int i = 0; i < off.length; i++) {
            if (off[i] != null) {
                off[i].setVisibility(View.INVISIBLE);
            }
        }
        for (int i = 0; i < on.length; i++) {
            if (on[i] != null) {
                on[i].setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Enforce roman numeral writing conventions
     * and enable and disable buttons as the user
     * enters roman numerals
     */
    public void romanRules() {
        if (numberEntered.length() < 1) {
            return;
        }

        String curChar = numberEntered.substring(numberEntered.length() - 1);
        int curCharIndex = 0;
        int startIndex = -1; //keep track of how many buttons to disable

        Button curButton = null;
        for (int i = 0; i < romanValues.length; i++) {
            if (romanValues[i].equals(curChar)) {
                curButton = romanButtons[i];
                curCharIndex = i;
            }
        }

        //"V", "L" and "D" are never subtracted or repeated
        if (curChar.equals("V") || curChar.equals("L") || curChar.equals("D")) {
            startIndex = curCharIndex;
        }
        //"I" & "X" can only be subtracted from the next 2 larger numerals
        else if (curChar.equals("I") || curChar.equals("X")) {
            //in case some valid numerals were previously disabled
            if (!numberEntered.contains("V") && !numberEntered.contains("L")) {
                enableRomanButtons();
            }
            startIndex = curCharIndex + 3;
        }

        if (numberEntered.length() >= 2) {
            String secondLastChar =
                    numberEntered.substring(numberEntered.length()-2, numberEntered.length()-1);

            int secondCharIndex;
            for (int i = 0; i < romanValues.length; i++) {
                if (romanValues[i].equals(secondLastChar)) {
                    secondCharIndex = i;
                    if (secondCharIndex < curCharIndex) { //if a subtraction is taking place
                        //buttons will be disabled starting from the smaller numeral
                        startIndex = secondCharIndex;
                    }
                }
            }

            //if numerals are repeated
            if (secondLastChar.equals(curChar)) {
                //disable larger numerals since you can't subtract twice
                startIndex = curCharIndex + 1;
            }

            //limit all numerals except "M" to three occurrences
            if (numberEntered.length() >= 3 && !curChar.equals("M")) {
                String thirdLastChar =
                        numberEntered.substring(numberEntered.length()-3, numberEntered.length()-2);
                if (thirdLastChar.equals(secondLastChar) && secondLastChar.equals(curChar)) {
                    curButton.setClickable(false);
                    curButton.setAlpha(0.5f);
                }
            }
        }
        //if buttons need to be disabled
        if (startIndex > -1) {
            //loop through array of buttons and disable buttons as required
            for (int i = startIndex; i < romanButtons.length; i++) {
                romanButtons[i].setClickable(false);
                romanButtons[i].setAlpha(0.5f);
            }
        }
    }

    /**
     * Loop through array of roman numeral buttons
     * and enable each button as well as turn off
     * transparency
     */
    public void enableRomanButtons() {
        for (int i = 0; i < romanButtons.length; i++) {
            romanButtons[i].setClickable(true);
            romanButtons[i].setAlpha(1f);
        }
    }

    /**
     * Check if conditions for entering numbers
     * have been met and set booleans accordingly
     */
    public void checkTypingConditions() {
        //if an operation button was pressed
        if (calculation) {
            display.setText(""); //clear display
            firstNumberEntered = numberEntered; //save first number
            numberEntered = ""; //clear first number to hold second number
            calculation = false;
            readyToCalculate = true; //equals button can now be pressed
        }

        //if calculation was completed
        if (calculationDone) {
            //operate directly on the answer
            if (!operationSelected) {
                firstNumberEntered = numberEntered;
            }
            //clear if user decides to enter new numbers
            numberEntered = "";
            display.setText("");
            calculationDone = false;
        }

        //clear answer display after a calculation
        if (operation.equals("")) {
            answerDisplay.setText("");
        }
    }

    /**
     * Reset all values to initial state
     */
    public void resetValues() {
        display.setText("");
        answerDisplay.setText("");
        numberEntered = "";
        firstNumberEntered = "";
        operation = "";
        calculation = false;
        readyToCalculate = false;
        calculationDone = false;
        operationSelected = false;
        enableRomanButtons();
    }

    /**
     * Check whether within 1 - 4999 limit
     * @throws InvalidNumberException
     */
    public void checkLimit () throws InvalidNumberException {
        int n;
        if (romanButtonsOn) {
            n = Roman.convertToInt(numberEntered); //will return 0 if roman numeral is invalid
        } else {
            n = Integer.parseInt(numberEntered);
        }

        if (n < 1 || n > 4999) {
            throw new InvalidNumberException("Out Of Range (1 to 4999)");
        }
    }

    /**
     * Overloaded method to check whether
     * parameter passed is within the limit
     * @throws InvalidNumberException
     */
    public void checkLimit (int n) throws InvalidNumberException {
        if (n < 1 || n > 4999) {
            throw new InvalidNumberException("Out Of Range (1 to 4999)");
        }
    }
}