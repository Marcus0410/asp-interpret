package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeListValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspForStmt extends AspCompoundStmt {
    AspName name;
    AspExpr expr;
    AspSuite suite;

    AspForStmt(int n) {
        super(n);
    }

    static AspForStmt parse(Scanner s) {
        enterParser("for stmt");
        AspForStmt afs = new AspForStmt(s.curLineNum());

        skip(s, forToken);
        afs.name = AspName.parse(s);
        skip(s, inToken);
        afs.expr = AspExpr.parse(s);
        skip(s, colonToken);
        afs.suite = AspSuite.parse(s);
        leaveParser("for stmt");
        return afs;
    }

    @Override
    void prettyPrint() {
        prettyWrite("for ");
        name.prettyPrint();
        prettyWrite(" in ");
        expr.prettyPrint();
        prettyWrite(":");
        suite.prettyPrint();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeListValue list = new RuntimeListValue(null);

        RuntimeValue exprEval = expr.eval(curScope);
        // make sure expr is a list
        if (exprEval instanceof RuntimeListValue) {
            list = (RuntimeListValue) exprEval;
        } else {
            RuntimeValue.runtimeError("For loop range is not a list!", this);
        }
        ArrayList<RuntimeValue> forList = list.getList("for stmt", this);

        // loop through loop range
        for (int i = 0; i < forList.size(); i++) {
            curScope.assign(name.name, forList.get(i)); // update loop variable
            trace("for #" + (i + 1) + ": " + name.name + " = " + curScope.find(name.name, this).showInfo());
            suite.eval(curScope);
        }
        return null;
    }

}
