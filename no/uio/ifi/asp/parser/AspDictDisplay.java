package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeDictValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AspDictDisplay extends AspAtom {
    ArrayList<AspStringLiteral> stringLiterals = new ArrayList<>();
    ArrayList<AspExpr> exprs = new ArrayList<>();

    AspDictDisplay(int n) {
        super(n);
    }

    static AspDictDisplay parse(Scanner s) {
        enterParser("dict display");
        skip(s, leftBraceToken);

        AspDictDisplay add = new AspDictDisplay(s.curLineNum());
        while (true) {
            if (s.curToken().kind == rightBraceToken)
                break;

            add.stringLiterals.add(AspStringLiteral.parse(s));

            skip(s, colonToken);

            add.exprs.add(AspExpr.parse(s));

            if (s.curToken().kind != commaToken)
                break;

            skip(s, commaToken);
        }

        skip(s, rightBraceToken);

        leaveParser("dict display");
        return add;
    }

    @Override
    void prettyPrint() {
        prettyWrite("{");

        for (int i = 0; i < stringLiterals.size(); i++) {
            if (i > 0) {
                prettyWrite(", ");
            }

            stringLiterals.get(i).prettyPrint();
            prettyWrite(":");
            exprs.get(i).prettyPrint();
        }

        prettyWrite("}");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        HashMap<String, RuntimeValue> hMap = new HashMap<>();

        // add keys and values to hMap
        for (int i = 0; i < stringLiterals.size(); i++) {
            hMap.put(stringLiterals.get(i).str, exprs.get(i).eval(curScope));
        }

        RuntimeDictValue dict = new RuntimeDictValue(hMap);
        return dict;
    }

}
