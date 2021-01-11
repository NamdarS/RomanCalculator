package com.example.romanconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
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
    String secondNumberEntered = "";
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

    //roman conversion object
    Roman roman;

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

        //create roman object for conversions and calculations
        roman = new Roman();

    }

    public void numberButtonClick(View view) {
        buttonAnimation(view);
        if (checkLimit()) {
            String message = "Can't enter more than 12 digits";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        view.performHapticFeedback(1);
        if (!romanButtonsOn) {
            if (!decimalDisplay) {
                numberEntered = "";
                display.setText("");
                decimalDisplay = true;
            }

            checkTypingConditions();

            Button curButton = (Button) view;
            int curValue = decimalButtonValues.get(curButton.getId());
            display.append(String.valueOf(curValue));
            numberEntered += String.valueOf(curValue);
        }
    }

    public void romanButtonClick(View view) {
        buttonAnimation(view);
        if (checkLimit()) {
            String message = "Can't enter more than 12 numbers";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        view.performHapticFeedback(1);
        if (romanButtonsOn) {
            if (decimalDisplay) {
                numberEntered = "";
                display.setText("");
                decimalDisplay = false;
            }

            checkTypingConditions();

            Button curButton = (Button) view;
            String curValue = romanButtonValues.get(curButton.getId());
            display.append(curValue);
            numberEntered += curValue;
        }
    }

    public void deleteClick(View view) {
        buttonAnimation(view);
        view.performHapticFeedback(1);

        if (display.length() > 0) {
            numberEntered = numberEntered.substring(0, numberEntered.length() - 1);
            display.setText(numberEntered);
        }
    }

    public void operationClick(View view) {
        buttonAnimation(view);
        view.performHapticFeedback(1);

        if (numberEntered.length() > 0) {
            String message =  "Convert back to do operations";
            if (decimalDisplay != romanButtonsOn) {
                if (!calculation && !readyToCalculate ||calculationDone) {
                    operation = (String) view.getTag();
                    calculation = true;
                    operationSelected = true;
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
        view.performHapticFeedback(1);

        if (readyToCalculate) {
            double answer;
            double firstNumber;
            double secondNumber;


            if (romanButtonsOn) {
                firstNumber = roman.convertToInt(secondNumberEntered);
                secondNumber = roman.convertToInt(numberEntered);
                if (firstNumber == 0 || secondNumber == 0) {
                    String message = "Invalid Roman numeral entered";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    resetValues();
                    return;
                }
            } else {
                firstNumber = Double.parseDouble(secondNumberEntered);
                secondNumber = Double.parseDouble(numberEntered);
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

            answer = Math.round(answer * 100.0) / 100.0;
            numberEntered = String.valueOf(answer);

            if (romanButtonsOn) {
                if (answer < 1) {
                    String message = "Roman numerals can't be negative or decimals";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    resetValues();
                    return;
                } else {
                    numberEntered = roman.convertToString((int) answer);
                }
            }

            readyToCalculate = false;
            calculationDone = true;
            operation = "";
            secondNumberEntered = "";

            if (checkLimit()) {
                String message = "Too big to compute";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                resetValues();
                return;
            }

            display.setText("");
            answerDisplay.setText(numberEntered);
        }
    }

    public void convertValueClick(View view) {
        buttonAnimation(view);
        view.performHapticFeedback(1);
        if (!calculation || !readyToCalculate) {
            if (numberEntered.length() > 0) {
                if (decimalDisplay) {
                    double value = Double.parseDouble(numberEntered);
                    if (value < 0) {
                        String message = "Roman numerals can't be negative";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else if (value % 1 != 0)  {
                        String message = "Only whole numbers can be converted to Roman";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } {
                        numberEntered = roman.convertToString((int) value);
                        display.setText(numberEntered);
                        decimalDisplay = false;
                    }
                } else {
                    if (String.valueOf(roman.convertToInt(numberEntered)).equals("0")) {
                        String message = "Invalid Roman numeral entered";
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        numberEntered = String.valueOf(roman.convertToInt(numberEntered));
                        display.setText(numberEntered);
                        decimalDisplay = true;
                    }

                }
            }
        }
    }

    public void changeButtonsClick(View view) {
        buttonAnimation(view);
        answerDisplay.setText("");
        view.performHapticFeedback(1);
        buttonsDisplayed = (String) view.getTag();

        if (buttonsDisplayed.equals("roman") && !romanButtonsOn) {
            String message = "Calculations with roman numerals are rounded";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            toggleButtons(romanButtons, decimalButtons);
            romanButtonsOn = true;
            decimalDisplay = false;
        }

        else if (buttonsDisplayed.equals("decimal") && romanButtonsOn) {
            toggleButtons(decimalButtons, romanButtons);
            romanButtonsOn = false;
            decimalDisplay = true;
        }

        resetValues();
    }

    public void clearClick(View view) {
        buttonAnimation(view);
        view.performHapticFeedback(1);
        resetValues();
    }

    public void buttonAnimation(View view) {
        float pivotX = (float) view.getWidth() / 2;
        float pivotY = (float) view.getHeight() / 2;
        ScaleAnimation animation = new ScaleAnimation(0.9f, 1f, 0.9f, 1f, pivotX, pivotY);
        animation.setDuration(150);
        view.startAnimation(animation);
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

    public void checkTypingConditions() {
        if (calculation) {
            display.setText("");
            secondNumberEntered = numberEntered;
            numberEntered = "";
            calculation = false;
            readyToCalculate = true;
        }

        if (calculationDone) {
            if (!operationSelected) {
                secondNumberEntered = numberEntered;
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
        secondNumberEntered = "";
        operation = "";
        boolean calculation = false;
        boolean readyToCalculate = false;
        boolean calculationDone = false;
        boolean operationSelected = false;
    }

    public boolean checkLimit () {
        if (numberEntered.length() > 11 || numberEntered.contains("E")) {
            return true;
        }
        return false;
    }


}