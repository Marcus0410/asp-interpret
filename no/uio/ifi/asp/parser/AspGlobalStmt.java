package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspGlobalStmt extends AspSmallStmt {
    ArrayList<AspName> names = new ArrayList<>();

    AspGlobalStmt(int n) {
        super(n);
    }

    static AspGlobalStmt parse(Scanner s) {
        enterParser("global stmt");

        AspGlobalStmt ags = new AspGlobalStmt(s.curLineNum());

        skip(s, globalToken);

        while (true) {
            ags.names.add(AspName.parse(s));

            if (s.curToken().kind != commaToken)
                break;

            skip(s, commaToken);
        }

        leaveParser("global stmt");
        return ags;
    }

    @Override
    void prettyPrint() {
        prettyWrite("global ");

        int nPrinted = 0;
        for (AspName name : names) {
            if (nPrinted > 0)
                prettyWrite(", ");

            name.prettyPrint();
            nPrinted++;
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        for (AspName name : names) {
            curScope.registerGlobalName(name.name);
        }
        return null;
    }

}
