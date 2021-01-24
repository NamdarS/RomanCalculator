package com.example.romanconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
    //decimal buttons
    Button decimal0, decimal1, decimal2, decimal3, decimal4,
           decimal5, decimal6, decimal7, decimal8, decimal9;
    Button[] decimalButtons;

    //roman buttons and values
    Button roman0, roman1, roman2, roman3, roman4,
           roman5, roman6;
    Button[] romanButtons;
    String[] romanValues;

    //numbers and operations
    String numberEntered = "";
    String firstNumberEntered = "";
    String operation = "";
    String buttonsDisplayed = "";

    //keeping track of operations and number system
    boolean calculation = false;
    boolean readyToCalculate = false;
    boolean calculationDone = false;
    boolean romanButtonsOn = false;
    boolean operationSelected = false;
    boolean decimalDisplay = true;

    //display and data storage
    TextView display;
    TextView answerDisplay;
    Hashtable<Integer, Integer> decimalButtonValues;
    Hashtable<Integer, String> romanButtonValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.textView);
        answerDisplay = findViewById(R.id.answerTextView);
        decimalButtonValues = new Hashtable<>();
        romanButtonValues = new Hashtable<>();

        //decimal buttons array
        decimalButtons = new Button[]{decimal0, decimal1, decimal2, decimal3, decimal4,
                            decimal4, decimal5, decimal6, decimal7, decimal8, decimal9};

        //find decimal buttons using ids and add to hashtable
        for (int i = 0; i < decimalButtons.length; i++) {
            int id = getResources().getIdentifier("button" + i, "id", getPackageName());
            decimalButtons[i] = findViewById(id);
            decimalButtonValues.put(id, i);
        }

        //roman buttons and values arrays
        romanButtons = new Button[]{roman0, roman1, roman2, roman3, roman4, roman5, roman6};
        romanValues = new String[]{"I", "V", "X", "L", "C", "D", "M"};

        //find roman buttons using ids and add to hashtable
        for (int i = 0; i < romanButtons.length; i++) {
            int id = getResources().getIdentifier("roman" + i, "id", getPackageName());
            romanButtons[i] = findViewById(id);
            romanButtonValues.put(id, romanValues[i]);
        }
    }

    public void numberButtonClick(View view) {
        buttonAnimation(view);

        if (!romanButtonsOn) {
            if (!decimalDisplay) {
                numberEntered = "";
                display.setText("");
                decimalDisplay = true;
            }

            checkTypingConditions();

            Button curButton = (Button) view;
            int curValue = decimalButtonValues.get(curButton.getId());
            numberEntered += String.valueOf(curValue);
            if (checkLimit()) {
                String message = "Can't exceed limit of 4999";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                numberEntered = numberEntered.substring(0, numberEntered.length() - 1);
                return;
            }
            display.append(String.valueOf(curValue));
        }
    }

    public void romanButtonClick(View view) {
        buttonAnimation(view);

        if (romanButtonsOn) {
            if (decimalDisplay) {
                numberEntered = "";
                display.setText("");
                decimalDisplay = false;
            }

            checkTypingConditions();

            Button curButton = (Button) view;
            String curValue = romanButtonValues.get(curButton.getId());
            numberEntered += curValue;
            if (checkLimit()) {
                String message = "Can't exceed limit of 4999";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                numberEntered = numberEntered.substring(0, numberEntered.length() - 1);
                return;
            }
            romanRules();
            display.append(curValue);
        }
    }

    public void deleteClick(View view) {
        buttonAnimation(view);

        if (display.length() > 0) {
            numberEntered = numberEntered.substring(0, numberEntered.length() - 1);
            display.setText(numberEntered);
            enableRomanButtons();
            romanRules();
        }
    }

    public void operationClick(View view) {
        buttonAnimation(view);
        String message = "";

        if (numberEntered.length() > 0) {
            message =  "Convert back to do operations";
            if (decimalDisplay != romanButtonsOn) {
                if (!calculation && !readyToCalculate ||calculationDone) {
                    operation = (String) view.getTag();
                    calculation = true;
                    operationSelected = true;
                    if (operation.equals("divide")) {
                        message = "Quotient will be rounded";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    enableRomanButtons();
                    return;
                } else {
                    message = "One operation at a time";
                }
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

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

            if (operation.equals("add")) {
                answer = firstNumber + secondNumber;
            } else if (operation.equals("subtract")) {
                answer = firstNumber - secondNumber;
            } else if (operation.equals("multiply")) {
                answer = firstNumber * secondNumber;
            } else {
                answer = firstNumber / secondNumber;
            }

            if (answer < 1) {
                String message = "Negative answers not permitted";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                resetValues();
                return;
            }

            if (romanButtonsOn) {
                numberEntered = Roman.convertToString(answer);
            } else {
                numberEntered = String.valueOf(answer);
            }

            if (checkLimit()) {
                String message = "Can't exceed limit of 4999";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                resetValues();
                return;
            }

            answerDisplay.setText(numberEntered);
            display.setText("");
            readyToCalculate = false;
            calculationDone = true;
            operation = "";
            firstNumberEntered = "";
            enableRomanButtons();
        }
    }

    public void convertValueClick(View view) {
        buttonAnimation(view);
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

    public void changeButtonsClick(View view) {
        buttonAnimation(view);
        answerDisplay.setText("");
        buttonsDisplayed = (String) view.getTag();

        if (buttonsDisplayed.equals("roman") && !romanButtonsOn) {
            toggleButtons(romanButtons, decimalButtons);
            romanButtonsOn = true;
            decimalDisplay = false;
        } else if (buttonsDisplayed.equals("decimal") && romanButtonsOn) {
            toggleButtons(decimalButtons, romanButtons);
            romanButtonsOn = false;
            decimalDisplay = true;
        }

        resetValues();
    }

    public void clearClick(View view) {
        buttonAnimation(view);
        resetValues();
        enableRomanButtons();
    }

    public void switchActivityClick(View view) {
        buttonAnimation(view);
        Intent intent = new Intent(MainActivity.this, RomanDefinitionsActivity.class);
        startActivity(intent);
    }

    public void buttonAnimation(View view) {
        float pivotX = (float) view.getWidth() / 2;
        float pivotY = (float) view.getHeight() / 2;
        ScaleAnimation animation =
                new ScaleAnimation(0.9f, 1f, 0.9f, 1f, pivotX, pivotY);
        animation.setDuration(150);
        view.startAnimation(animation);
        view.performHapticFeedback(1);
    }

    public void toggleButtons (Button[] on, Button[] off) {
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

    public void romanRules() {
        if (numberEntered.length() < 1) {
            return;
        }

        String curChar = numberEntered.substring(numberEntered.length() - 1);
        int curCharIndex = 0;
        int startIndex = -1;

        Button curButton = null;
        for (int i = 0; i < romanValues.length; i++) {
            if (romanValues[i].equals(curChar)) {
                curButton = romanButtons[i];
                curCharIndex = i;
            }
        }

        if (curChar.equals("V") || curChar.equals("L") || curChar.equals("D")) {
            startIndex = curCharIndex;
        } else if (curChar.equals("I") || curChar.equals("X")) {
            startIndex = curCharIndex + 3;
        }

        if (numberEntered.length() >= 2) {
            String secondLastChar =
                    numberEntered.substring(numberEntered.length()-2, numberEntered.length()-1);

            int secondCharIndex;
            for (int i = 0; i < romanValues.length; i++) {
                if (romanValues[i].equals(secondLastChar)) {
                    secondCharIndex = i;
                    if (secondCharIndex < curCharIndex) {
                        startIndex = secondCharIndex;
                    }
                }
            }

            if (secondLastChar.equals(curChar)) {
                startIndex = curCharIndex + 1;
            }

            if (numberEntered.length() >= 3 && !curChar.equals("M")) {
                String thirdLatChar =
                        numberEntered.substring(numberEntered.length()-3, numberEntered.length()-2);
                if (thirdLatChar.equals(secondLastChar) && secondLastChar.equals(curChar)) {
                    curButton.setClickable(false);
                    curButton.setAlpha(0.5f);
                }
            }
        }

        if (startIndex > -1) {
            for (int i = startIndex; i < romanButtons.length; i++) {
                romanButtons[i].setClickable(false);
                romanButtons[i].setAlpha(0.5f);
            }
        }
    }
    
    public void enableRomanButtons() {
        for (int i = 0; i < romanButtons.length; i++) {
            romanButtons[i].setClickable(true);
            romanButtons[i].setAlpha(1f);
        }
    }

    public void checkTypingConditions() {
        if (calculation) {
            display.setText("");
            firstNumberEntered = numberEntered;
            numberEntered = "";
            calculation = false;
            readyToCalculate = true;
        }

        if (calculationDone) {
            if (!operationSelected) {
                firstNumberEntered = numberEntered;
            }
            numberEntered = "";
            display.setText("");
            calculationDone = false;
        }

        if (operation.equals("")) {
            answerDisplay.setText("");
        }
    }

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

    public boolean checkLimit () {
        int n;
        if (romanButtonsOn) {
            n = Roman.convertToInt(numberEntered);
        } else {
            n = Integer.parseInt(numberEntered);
        }
        if (n > 4999) {
            return true;
        }
        return false;
    }

}