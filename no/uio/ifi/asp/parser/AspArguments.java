package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeListValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;
import java.util.ArrayList;

public class AspArguments extends AspPrimarySuffix {
    ArrayList<AspExpr> exprs = new ArrayList<>();

    AspArguments(int n) {
        super(n);
    }

    static AspArguments parse(Scanner s) {
        enterParser("arguments");
        AspArguments aa = new AspArguments(s.curLineNum());

        skip(s, leftParToken);

        while (s.curToken().kind != rightParToken) {
            aa.exprs.add(AspExpr.parse(s));

            if (s.curToken().kind != commaToken)
                break;

            skip(s, commaToken);
        }

        skip(s, rightParToken);

        leaveParser("arguments");
        return aa;
    }

    @Override
    void prettyPrint() {
        prettyWrite("(");

        int nPrinted = 0;
        for (AspExpr expr : exprs) {
            if (nPrinted > 0)
                prettyWrite(", ");

            expr.prettyPrint();
            nPrinted++;
        }

        prettyWrite(")");

    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        ArrayList<RuntimeValue> list = new ArrayList<>();

        for (int i = 0; i < exprs.size(); i++) {
            list.add(exprs.get(i).eval(curScope));
        }
        return new RuntimeListValue(list);
    }

}
