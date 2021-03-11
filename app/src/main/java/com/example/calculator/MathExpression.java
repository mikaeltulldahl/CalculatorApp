package com.example.calculator;

public class Eval {

    private enum Operator {
        INC("++", 1, true){ public double execute(double lhs, double rhs) { return lhs + 1; }},
        ADD("+", 1, true){ public double execute(double lhs, double rhs) { return lhs + rhs; }},
        DEC("--", 1, true){ public double execute(double lhs, double rhs) { return lhs - 1; }},
        SUB("-", 1, true){ public double execute(double lhs, double rhs) { return lhs - rhs; }},
        MUL("*", 2, true){ public double execute(double lhs, double rhs) { return lhs * rhs; }},
        DIV("/", 2, true){ public double execute(double lhs, double rhs) { return lhs / rhs; }},
        EXP("^",3, false){ public double execute(double lhs, double rhs) { return Math.pow(lhs, rhs); }},
        SQRT("sqrt",1, true){ public double execute(double lhs, double rhs) { return Math.sqrt(rhs); }},
        SIN("sin",1, true){ public double execute(double lhs, double rhs) { return Math.sin(rhs); }},
        COS("cos",1, true){ public double execute(double lhs, double rhs) { return Math.cos(rhs); }},
        TAN("tan",1, true){ public double execute(double lhs, double rhs) { return Math.tan(rhs); }},
        ASIN("asin",1, true){ public double execute(double lhs, double rhs) { return Math.asin(rhs); }},
        ACOS("acos",1, true){ public double execute(double lhs, double rhs) { return Math.acos(rhs); }},
        ATAN("atan",1, true){ public double execute(double lhs, double rhs) { return Math.atan(rhs); }},
        LOG("log",1, true){ public double execute(double lhs, double rhs) { return Math.log(rhs); }},
        LOG10("log10",1, true){ public double execute(double lhs, double rhs) { return Math.log10(rhs); }},
        ABS("abs",1, true){ public double execute(double lhs, double rhs) { return Math.abs(rhs); }},
        RAND("rand",1, true){ public double execute(double lhs, double rhs) { return Math.random(); }},
        MOD("%",1, true){ public double execute(double lhs, double rhs) { return lhs % rhs; }},
        PI("pi",10, true){ public double execute(double lhs, double rhs) { return Math.PI; }};

        public final String type;
        public final int precedence;
        public final boolean leftAssociate;
        public final int length;

        Operator(String type, int precedence, boolean leftAssociate) {
            this.type = type;
            this.precedence = precedence;
            this.leftAssociate = leftAssociate;
            this.length = this.type.length();
        }

        public static Operator get(String s, int start) {
            for (Operator e : values()) {
                if (s.startsWith(e.type, start)) {
                    return e;
                }
            }
            return null;
        }

        public abstract double execute(double lhs, double right);
    }

    public static class Token {
        double val;
        final Operator operator;
        int end; //idx of last char in string

        Token(double v, int idx){//value
            val = v;
            end = idx;
            operator = null;
        }

        Token(Operator operator, int end){//operator
            this.operator = operator;
            this.end = end;
        }

        /*
         *  returns the next operator found in string, either an operator, a value or end of string == ')'
         */
        private static Token getNext(String s, int start, int end){
            if(start < end){
                Operator t = Operator.get(s,start);
                if(t != null) {
                    return new Token(t, start + t.length);
                }
                switch(s.charAt(start)){
                    case '(':
                        Token v = evalExpression(s, start + 1, end);
                        v.end = v.end + 1;
                        if(v.end <= end && s.charAt(v.end - 1) == ')'){
                            return v;
                        }
                        return null;
                    case ')':
                        return null;
                    default :
                        // in case current char is a digit, read the whole integer
                        int startIdx = start;
                        while (start < end && (Character.isDigit(s.charAt(start)) || s.charAt(start) == '.')) {
                            start++;
                        }
                        return new Token(Double.parseDouble(s.substring(startIdx, start)), start);
                }
//                //insert implied * after ")"
//                if (s.charAt(start) == '(') {
//                    return new Token(Operator.MUL, start);
//                }
//
//                //insert implied * before "("
//                if (start >= 1 && s.charAt(start - 1) == ')') {
//                    return new Token(Operator.MUL, start);
//                }
            }
            return null;
        }

        private Token execute(Token lhs, Token rhs) {
            //TODO sometimes operator will not consume rhs or lhs
            return new Token(operator.execute(lhs.val, rhs.val), rhs.end);
        }
    }

    public static double evalExpression(String s) {
        Token o = evalExpression(s, 0, s.length());
        if(o.end != s.length()){
            throw new IllegalArgumentException("mismatching parentheses");
        }
        return o.val;
    }

    private static Token evalExpression(String s, int start, int end) {
        Token lhs = Token.getNext(s, start, end);
        Token lookAheadOp = Token.getNext(s, lhs.end, end);
        return evalExpressionHelper(s, lhs, lookAheadOp, 0, end);
    }

    private static Token evalExpressionHelper(String s, Token lhs, Token lookAheadOp, int min_precedence, int end) {
        while(lookAheadOp != null && lookAheadOp.operator.precedence >= min_precedence) {// lookAheadOp is a binary operator whose precedence is >=min_precedence
            Token op = lookAheadOp;
            int start = lookAheadOp.end;
            Token rhs = Token.getNext(s, start, end);
            if(rhs == null){
                throw new IllegalArgumentException("bad input");
            }
            start = rhs.end;
            lookAheadOp = Token.getNext(s, start, end);//peek next operator
            if(lookAheadOp != null && lookAheadOp.operator.precedence > op.operator.precedence) {//lookAheadOp is a binary operator whose precedence is greater than op 's, or a right-associative operator whose precedence is equal to op 's
                rhs = evalExpressionHelper(s, rhs, lookAheadOp,op.operator.precedence + 1, end);
                start = rhs.end;
                lookAheadOp = Token.getNext(s, start, end);//peek next operator
            }
            lhs = op.execute(lhs, rhs);//the result of applying op with operands lhs and rhs
        }
        return lhs;
    }
}
