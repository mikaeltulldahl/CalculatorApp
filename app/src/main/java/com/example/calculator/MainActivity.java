package com.example.calculator;

import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView output;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = findViewById(R.id.output);
        input = findViewById(R.id.input);

        output.setMovementMethod(new ScrollingMovementMethod());

        output.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                output.setText(getResources().getString(R.string.empty));
                return true;
            }
        });
    }

    public void evalButtonClicked(View v){
        String inputString = input.getText().toString().replaceAll("\\s","");//remove whitespaces
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    double val = new MathExpression(inputString).evaluate();
                    DecimalFormat formatter = new DecimalFormat("0.##");
                    String newString = String.format("%s = %s", inputString, formatter.format(val));
                    if(output.getText().toString().isEmpty()){
                        output.setText(newString);
                    }else{
                        output.append("\n" + newString);
                    }
                    input.setText(getResources().getString(R.string.empty));
                }catch (IllegalArgumentException e) {
                    Toast.makeText(getApplicationContext(),"Invalid input",Toast.LENGTH_SHORT).show();
                }
                catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(),"NullPointerException",Toast.LENGTH_SHORT).show();
                }
            }
        };
        Handler handler = new Handler();
        handler.post(runnable);
    }
}