package com.example.hptouchsmart.codinblock_calci;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView ansTV;
    TextView numberEnter;
    TextView evalNumber;
    String val = "";

    public void clearAll(View view){
        val = "";
        evalNumber.setText("0");
        ansTV.setText("");

    }


    public  void changeValue(View view){
        numberEnter = (TextView)view;
        //numberEnter.getText();
        //int number = Integer.parseInt(numberEnter.getTag().toString());
        char numEn = (char) numberEnter.getText().charAt(0);

            if (!val.isEmpty() && ((numEn == '+' || numEn == '-' || numEn == '*' || numEn == '/') && ((val.charAt(val.length() - 1) == '+') ||
                    (val.charAt(val.length() - 1) == '-') ||
                    (val.charAt(val.length() - 1) == '*') ||
                    (val.charAt(val.length() - 1) == '/')))) {

                val = val.substring(0, val.length() - 1) + numEn;
                evalNumber.setText(val);
            } else if (val.isEmpty() && (numEn == '+' ||
                    numEn == '-' ||
                    numEn == '*' ||
                    numEn == '/')) {
            } else {
                val += (String) numberEnter.getText();
                evalNumber.setText(val);
            }




        //Log.i("get Tag", String.valueOf(val.charAt(val.length() - 1)));



        // numberEnter.setText(val);

     }

    public void calculate(View view){
        if(val.isEmpty()){
            Toast.makeText(MainActivity.this, "Empty Input", Toast.LENGTH_SHORT).show();
        }else{


            Log.i("log",val);
            double ans = eval(val);
            ansTV.setText(Double.toString(ans));
            //Toast.makeText(MainActivity.this, Double.toString(ans), Toast.LENGTH_SHORT).show();
           // numberEnter.setText(Integer.toString(ans));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //numberEnter = (TextView) findViewById(R.id.tv_numbers);
        evalNumber = (TextView) findViewById(R.id.tv_number_eval);
        ansTV = (TextView)findViewById(R.id.ans);
    }


    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }


}
