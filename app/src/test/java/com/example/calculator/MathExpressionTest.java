package com.example.calculator;

import android.icu.text.DecimalFormat;
import android.widget.Toast;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EvalTest {
    private final double eps = 1e-7;

    @Test
    void evalExpression() {
        assertEquals(4, 2 + 2);

        testStringInput("1+1", 2);
        testStringInput("-1+1", 0);
        testStringInput("-1+2*(2+3)", 9);

        testBadStringInput("(");
        testBadStringInput("abc");
        testBadStringInput(" ");
        testBadStringInput("");
    }

    void testStringInput(String input, double expectedResult){
        try {
            double val = Eval.evalExpression(input);
            assertEquals(expectedResult, val, eps);
        }catch (IllegalArgumentException e) {
            Assertions.fail("IllegalArgumentException thrown");
        }catch (NullPointerException e) {
            Assertions.fail("NullPointerException thrown");
        }
    }

    public void testBadStringInput(String badInput) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Eval.evalExpression(badInput);
        });
    }
}