package ru.fizteh.fivt.students.AlexeyZhuravlev.calculator;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * @author AlexeyZhuravlev
 */

public final class BinaryMinusOperator extends Lexeme {

    @Override
    protected int priority() {
        return 1;
    }

    @Override
    protected void makeOperation(Stack<NumberLexeme> results) throws Exception {
        try {
            NumberLexeme second = results.pop();
            NumberLexeme first = results.pop();
            results.push(new NumberLexeme(first.value.subtract(second.value)));
        } catch (EmptyStackException e) {
            throw new Exception("Not enough arguments for binary minus operation");
        }
    }
}
