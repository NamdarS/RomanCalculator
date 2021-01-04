package com.example.romanconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
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
    boolean decimalDisplay = true;
    boolean operationSelected = false;

    //display and data storage
    TextView display;
    Hashtable<Integer, Integer> decimalButtonValues;
    Hashtable<Integer, String> romanButtonValues;

    //roman conversion object
    Roman roman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.textView);
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

    public void numberButtonClick (View view) {
        if (!romanButtonsOn) {
            if (!decimalDisplay) {
                numberEntered = "";
                display.setText("");
                decimalDisplay = true;
            }

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

            Button curButton = (Button) view;
            int curValue = decimalButtonValues.get(curButton.getId());
            display.append(String.valueOf(curValue));
            numberEntered += String.valueOf(curValue);
        }
    }

    public void romanButtonClick (View view) {
        if (romanButtonsOn) {
            if (decimalDisplay) {
                numberEntered = "";
                display.setText("");
                decimalDisplay = false;
            }

            if (calculation) {
                display.setText("");
                secondNumberEntered = numberEntered;
                numberEntered = "";
                calculation = false;
                readyToCalculate = true;
            }

            Button curButton = (Button) view;
            String curValue = romanButtonValues.get(curButton.getId());
            display.append(curValue);
            numberEntered += curValue;
        }
    }

    public void deleteClick (View view) {
        if (numberEntered.length() > 11) {
            numberEntered = numberEntered.substring(0, 12);
        }

        if (display.length() > 0) {
            numberEntered = numberEntered.substring(0, numberEntered.length() - 1);
            display.setText(numberEntered);
        }
    }

    public void operationClick (View view) {
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

    public void calculationClick (View view) {
        if (readyToCalculate) {
            double answer;
            double firstNumber;
            double secondNumber;


            if (romanButtonsOn) {
                firstNumber = roman.convertToInt(secondNumberEntered);
                secondNumber = roman.convertToInt(numberEntered);
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
                numberEntered = roman.convertToString((int) answer);
            }
            display.setText(numberEntered);

            readyToCalculate = false;
            calculationDone = true;
            operation = "";
            secondNumberEntered = "";
        }
    }

    public void convertValueClick (View view) {
        if (!calculation || !readyToCalculate) {
            if (decimalDisplay) {
                double value = Double.parseDouble(numberEntered);
                numberEntered = roman.convertToString((int) value);
                display.setText(numberEntered);
                decimalDisplay = false;
            } else {
                numberEntered = String.valueOf(roman.convertToInt(numberEntered));
                display.setText(numberEntered);
                decimalDisplay = true;
            }
        }
    }

    public void changeButtonsClick (View view) {
        buttonsDisplayed = (String) view.getTag();

        if (buttonsDisplayed.equals("roman") && !romanButtonsOn) {
            for (int i = 0; i < romanButtons.length; i++) {
                romanButtons[i].setVisibility(View.VISIBLE);
            }
            romanButtonsOn = true;
            decimalDisplay = false;
        }

        else if (buttonsDisplayed.equals("decimal") && romanButtonsOn) {
            for (int i = 0; i < romanButtons.length; i++) {
                romanButtons[i].setVisibility(View.INVISIBLE);
            }
            romanButtonsOn = false;
            decimalDisplay = true;
        }

        display.setText("");
        numberEntered = "";
        secondNumberEntered = "";
        operation = "";
        calculation = false;
        readyToCalculate = false;
    }
}