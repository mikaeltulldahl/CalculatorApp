package com.example.calculator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MathExpressionTest {
    private final double eps = 1e-5;

    @Test
    void evalExpression() {
        assertEquals(4, 2 + 2);

        testStringInput("1+1", 2);
        testStringInput("1+2*3", 7);
        testStringInput("sqrt2", 1.41421356237);
        testStringInput("sqrt(2)", 1.41421356237);
        testStringInput("5++", 6);
        testStringInput("1.3+2*(2+3)", 11.3);
        testStringInput("1.3+2*(2+3)", 11.3);
        testStringInput("2^3^2", 512);
        testStringInput("(2^3)^2", 64);
        testStringInput("2+3*2^1.5+1", 11.4852813742);
        testStringInput("2+3^2*1.5+1", 16.5);
        testStringInput("sqrt(sqrt(5^4))", 5);

        //testStringInput("-1+1", 0);
        //testStringInput("-1+2*(2+3)", 9);

        testBadStringInput("(");
        testBadStringInput("abc");
        testBadStringInput(" ");
        testBadStringInput("");
        testBadStringInput("(4 + 2");
        testBadStringInput("(4 + 2))");
    }

    void testStringInput(String input, double expectedResult){
        try {
            double val = new MathExpression(input).evaluate();
            assertEquals(expectedResult, val, eps);
        }catch (IllegalArgumentException e) {
            Assertions.fail("IllegalArgumentException thrown");
        }catch (NullPointerException e) {
            Assertions.fail("NullPointerException thrown");
        }
    }

    public void testBadStringInput(String badInput) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new MathExpression(badInput).evaluate();
        });
    }
}