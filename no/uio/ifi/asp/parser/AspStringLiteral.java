package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeStringValue;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspStringLiteral extends AspAtom {
    String str;

    AspStringLiteral(int n) {
        super(n);
    }

    static AspStringLiteral parse(Scanner s) {
        enterParser("string literal");
        AspStringLiteral asl = new AspStringLiteral(s.curLineNum());
        asl.str = s.curToken().stringLit;
        skip(s, stringToken);
        leaveParser("string literal");
        return asl;
    }

    @Override
    void prettyPrint() {
        char quotes = '"';
        if (str.contains("\"")) {
            quotes = '\'';
        }
        prettyWrite(quotes + str + quotes);

    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeStringValue v = new RuntimeStringValue(str);
        return v;
    }

}
