package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeListValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspListDisplay extends AspAtom {
    ArrayList<AspExpr> exprs = new ArrayList<>();

    AspListDisplay(int n) {
        super(n);
    }

    static AspListDisplay parse(Scanner s) {
        enterParser("list display");
        skip(s, leftBracketToken);

        AspListDisplay ald = new AspListDisplay(s.curLineNum());
        while (true) {
            if (s.curToken().kind == rightBracketToken)
                break;

            ald.exprs.add(AspExpr.parse(s));

            if (s.curToken().kind != commaToken)
                break;

            skip(s, commaToken);
        }

        skip(s, rightBracketToken);

        leaveParser("list display");
        return ald;
    }

    @Override
    void prettyPrint() {
        int nPrinted = 0;
        prettyWrite("[");

        for (AspExpr prettyExpr : exprs) {
            if (nPrinted > 0) {
                prettyWrite(", ");
            }
            prettyExpr.prettyPrint();
            nPrinted++;
        }
        prettyWrite("]");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        ArrayList<RuntimeValue> exprValues = new ArrayList<>();

        for (AspExpr e : exprs) {
            exprValues.add(e.eval(curScope));
        }

        return new RuntimeListValue(exprValues);
    }

}
