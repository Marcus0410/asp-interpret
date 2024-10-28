package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeFunc;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspFuncDef extends AspCompoundStmt {
    AspName funcName;
    ArrayList<AspName> parameters = new ArrayList<>();
    AspSuite suite;

    AspFuncDef(int n) {
        super(n);
    }

    static AspFuncDef parse(Scanner s) {
        enterParser("func def");
        AspFuncDef afd = new AspFuncDef(s.curLineNum());

        skip(s, defToken);

        afd.funcName = AspName.parse(s);

        skip(s, leftParToken);

        // get arguments
        while (s.curToken().kind != rightParToken) {
            afd.parameters.add(AspName.parse(s));

            // if more arguments
            if (s.curToken().kind == commaToken) {
                skip(s, commaToken);
            }
        }

        skip(s, rightParToken);
        skip(s, colonToken);

        afd.suite = AspSuite.parse(s);

        leaveParser("func def");
        return afd;
    }

    @Override
    void prettyPrint() {
        prettyWrite("def ");
        funcName.prettyPrint();
        prettyWrite(" (");

        int nPrinted = 0;
        for (AspName par : parameters) {
            if (nPrinted > 0)
                prettyWrite(", ");

            par.prettyPrint();
            nPrinted++;
        }

        prettyWrite(")");
        prettyWrite(":");
        suite.prettyPrint();
        prettyWriteLn();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue func = new RuntimeFunc(funcName.name, parameters, suite, curScope);
        trace("def " + funcName.name);
        return null;
    }

}
