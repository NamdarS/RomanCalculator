package com.example.romanconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    //decimal buttons
    Button decimal0;
    Button decimal1;
    Button decimal2;
    Button decimal3;
    Button decimal4;
    Button decimal5;
    Button decimal6;
    Button decimal7;
    Button decimal8;
    Button decimal9;

    //arithmetic buttons
    Button addition;
    Button subtraction;
    Button multiplication;
    Button division;


    String numberEntered = "";
    String secondNumberEntered = "";
    String operation = "";

    boolean calculation = false;
    boolean readyToCalculate = false;


    TextView display;
    Hashtable<Integer, Integer> decimalButtonValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.textView);
        decimalButtonValues = new Hashtable<>();

        //set decimal button ids
        Button[] decimalButtons = {decimal0, decimal1, decimal2, decimal3, decimal4,
                                decimal4, decimal5, decimal6, decimal7, decimal8, decimal9};

        //set decimal buttons and add to hashtable
        for (int i = 0; i < 10; i++) {
            int id = getResources().getIdentifier("button" + i, "id", getPackageName());
            decimalButtons[i] = findViewById(id);
            decimalButtonValues.put(id, i);
        }

        addition = findViewById(R.id.buttonAdd);

    }

    public void numberButtonClick (View view) {
        if (calculation) {
            display.setText("");
            secondNumberEntered = numberEntered;
            numberEntered = "";
            calculation = false;
            readyToCalculate = true;
        }

        Button curButton = (Button) view;
        int curValue = decimalButtonValues.get(curButton.getId());
        display.append(String.valueOf(curValue));
        numberEntered += String.valueOf(curValue);

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
        if (!calculation && !readyToCalculate) {
            operation = (String) view.getTag();
            calculation = true;
        } else {
            Toast.makeText(getApplicationContext(), "One operation at a time", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculationClick (View view) {
        if (readyToCalculate) {
            double answer = 0;

            if (operation.equals("add")) {
                answer = Double.parseDouble(secondNumberEntered) + Double.parseDouble(numberEntered);
            } else if (operation.equals("subtract")) {
                answer = Double.parseDouble(secondNumberEntered) - Double.parseDouble(numberEntered);
            } else if (operation.equals("multiply")) {
                answer = Double.parseDouble(secondNumberEntered) * Double.parseDouble(numberEntered);
            } else if (operation.equals("divide")) {
                answer = Double.parseDouble(secondNumberEntered) / Double.parseDouble(numberEntered);
            }

            answer = Math.round(answer * 100.0) / 100.0;
            numberEntered = String.valueOf(answer);
            display.setText(numberEntered);

            readyToCalculate = false;
            operation = "";
            secondNumberEntered = "";
        }
    }
}