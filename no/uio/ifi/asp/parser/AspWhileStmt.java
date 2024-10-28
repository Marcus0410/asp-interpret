package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspWhileStmt extends AspCompoundStmt {
    AspExpr expr;
    AspSuite suite;

    AspWhileStmt(int n) {
        super(n);
    }

    static AspWhileStmt parse(Scanner s) {
        enterParser("while stmt");
        AspWhileStmt aws = new AspWhileStmt(s.curLineNum());

        skip(s, whileToken);

        aws.expr = AspExpr.parse(s);

        skip(s, colonToken);

        aws.suite = AspSuite.parse(s);

        leaveParser("while stmt");
        return aws;
    }

    @Override
    void prettyPrint() {

        prettyWrite("while ");
        expr.prettyPrint();

        prettyWrite(": ");
        suite.prettyPrint();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue t = null;

        while (true) {
            t = expr.eval(curScope);

            // if test is false, stop loop
            if (!t.getBoolValue("while loop test", this))
                break;

            trace("while True: ...");
            suite.eval(curScope);
        }
        trace("while False:");
        return null;
    }

}
