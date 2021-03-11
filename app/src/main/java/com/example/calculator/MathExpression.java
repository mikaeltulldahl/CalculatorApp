package com.example.calculator;

import androidx.annotation.NonNull;

public class MathExpression {
    private final String s;
    NodeList<Token> tokenList = new NodeList<>();

    MathExpression(String input){
        s = input;
    }

    public double evaluate() {
        Token t = evaluate2(0);
        if(t.end != s.length()){
            throw new IllegalArgumentException("mismatching parentheses");
        }
        return t.val;
    }

    private Token evaluate2(int start) {
        Token t = getNextToken(start);
        while(t != null) {
            tokenList.add(t);
            t = getNextToken(t.end);
        }
        evalExpressionHelper2(getOperator(tokenList.head),0);
        if(tokenList.head == null || tokenList.head != tokenList.tail || tokenList.head.isOperator()){
            throw new IllegalArgumentException("invalid input");
        }
        return tokenList.head;
    }

    /*
     * inspired by https://en.wikipedia.org/wiki/Operator-precedence_parser#Pseudocode
     */
    private void evalExpressionHelper2(Token lookAheadOp, int min_precedence) {
        while(lookAheadOp != null && lookAheadOp.operator.precedence >= min_precedence) {// lookAheadOp is a binary operator whose precedence is >=min_precedence
            Token op = lookAheadOp;
            lookAheadOp = getNextOperator(op);//peek next operator
            //TODO unary operators
            while(doLookAheadFirst(lookAheadOp, op)) {
                int precedenceInc = lookAheadOp.operator.leftAssociate ? 1 : 0;
                evalExpressionHelper2(lookAheadOp, op.operator.precedence + precedenceInc);
                lookAheadOp = getNextOperator(lookAheadOp);//peek next operator
            }
            execute((Token)op.previous, op, (Token)op.next);//the result of applying op with operands lhs and rhs
        }
    }

    private boolean doLookAheadFirst(Token lookAheadOp, Token op){
        if(lookAheadOp == null){
            return false;
        }

        //lookAheadOp is a binary operator whose precedence is greater than op 's
        if(lookAheadOp.operator.precedence > op.operator.precedence) {
            return true;
        }

        //TODO should we really be looking at lookAheads's associativity?
        // or lookAheadOp is a right-associative operator whose precedence is equal to op 's
        return lookAheadOp.operator.precedence == op.operator.precedence && !lookAheadOp.operator.leftAssociate;
    }

    /*
     * finds the next operator after t
     */
    private Token getNextOperator(Token t){
        if(t != null) {
            return getOperator((Token) t.next);
        }else{
            return null;
        }
    }

    /*
     * finds the next operator including t
     */
    private Token getOperator(Token t){
        while (t != null && !t.isOperator()) {
            t = (Token) t.next;
        }
        return t;
    }

    /*
     *  returns the next operator found in string, either an operator, a value or end of string == ')'
     */
    private Token getNextToken(int start){
        if(start < s.length()){
            Operator t = Operator.get(s,start);
            if(t != null) {
                return new Token(t, start + t.length);
            }
            switch(s.charAt(start)){
                case '(':
                    Token v  = (new MathExpression(s)).evaluate2(start + 1);
                    v.end = v.end + 1;
                    if(v.end <= s.length() && s.charAt(v.end - 1) == ')'){
                        return v;
                    }
                    return null;
                case ')':
                    return null;
                default :
                    // in case current char is a digit, read the whole integer
                    int startIdx = start;
                    while (start < s.length() && (Character.isDigit(s.charAt(start)) || s.charAt(start) == '.')) {
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

    private enum Operator {
        INC("++", 1, true,true,false){ public double execute(double lhs, double rhs) { return lhs + 1; }},
        ADD("+", 1, true,true,true){ public double execute(double lhs, double rhs) { return lhs + rhs; }},
        DEC("--", 1, true,true,false){ public double execute(double lhs, double rhs) { return lhs - 1; }},
        SUB("-", 1, true,true,true){ public double execute(double lhs, double rhs) { return lhs - rhs; }},
        MUL("*", 2, true,true,true){ public double execute(double lhs, double rhs) { return lhs * rhs; }},
        DIV("/", 2, true,true,true){ public double execute(double lhs, double rhs) { return lhs / rhs; }},
        EXP("^",3, false,true,true){ public double execute(double lhs, double rhs) { return Math.pow(lhs, rhs); }},
        SQRT("sqrt",1, true,false,true){ public double execute(double lhs, double rhs) { return Math.sqrt(rhs); }},
        SIN("sin",1, true,false,true){ public double execute(double lhs, double rhs) { return Math.sin(rhs); }},
        COS("cos",1, true,false,true){ public double execute(double lhs, double rhs) { return Math.cos(rhs); }},
        TAN("tan",1, true,false,true){ public double execute(double lhs, double rhs) { return Math.tan(rhs); }},
        ASIN("asin",1, true,false,true){ public double execute(double lhs, double rhs) { return Math.asin(rhs); }},
        ACOS("acos",1, true,false,true){ public double execute(double lhs, double rhs) { return Math.acos(rhs); }},
        ATAN("atan",1, true,false,true){ public double execute(double lhs, double rhs) { return Math.atan(rhs); }},
        LOG("log",1, true,false,true){ public double execute(double lhs, double rhs) { return Math.log(rhs); }},
        LOG10("log10",1, true,false,true){ public double execute(double lhs, double rhs) { return Math.log10(rhs); }},
        ABS("abs",1, true,false,true){ public double execute(double lhs, double rhs) { return Math.abs(rhs); }},
        RAND("rand",1, true,false,false){ public double execute(double lhs, double rhs) { return Math.random(); }},
        MOD("%",1, true,true,true){ public double execute(double lhs, double rhs) { return lhs % rhs; }},
        PI("pi",10, true,false,false){ public double execute(double lhs, double rhs) { return Math.PI; }};

        public final String type;
        public final int precedence;
        public final boolean leftAssociate;
        public final int length;
        public final boolean consumesLHS;
        public final boolean consumesRHS;

        Operator(String type, int precedence, boolean leftAssociate, boolean consumesLHS, boolean consumesRHS) {
            this.type = type;
            this.precedence = precedence;
            this.leftAssociate = leftAssociate;
            this.length = this.type.length();
            this.consumesLHS = consumesLHS;
            this.consumesRHS = consumesRHS;
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

    private void execute(Token lhs, Token op, Token rhs) {
        //consume lhs & rhs
        double lhsVal = 0;
        double rhsVal = 0;
        if(op.operator.consumesLHS){
            lhsVal = lhs.val;
            tokenList.remove(lhs);
        }
        if(op.operator.consumesRHS){
            rhsVal = rhs.val;
            op.end = rhs.end;
            tokenList.remove(rhs);
        }
        double val = op.operator.execute(lhsVal, rhsVal);

        //convert Token from operator to value
        op.operator = null;
        op.val = val;
    }

    public static class Token extends ListNode {
        double val;
        Operator operator;
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

        boolean isOperator(){
            return operator != null;
        }

        @NonNull
        @Override
        public String toString() {
            if(isOperator()){
                return "Op: " + operator.type;
            }else{
                return "Val: " + val;
            }
        }
    }
}
