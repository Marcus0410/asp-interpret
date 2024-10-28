package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.Token;
import no.uio.ifi.asp.scanner.TokenKind;

public class AspCompOpr extends AspSyntax {
    Token opr;

    AspCompOpr(int n) {
        super(n);
    }

    static AspCompOpr parse(Scanner s) {
        enterParser("comp opr");

        AspCompOpr aco = new AspCompOpr(s.curLineNum());
        aco.opr = s.curToken();
        skip(s, aco.opr.kind);
        leaveParser("comp opr");
        return aco;
    }

    @Override
    void prettyPrint() {
        TokenKind k = opr.kind;
        switch (k) {
            case lessToken:
                prettyWrite("<");
                break;
            case greaterToken:
                prettyWrite(">");
                break;
            case doubleEqualToken:
                prettyWrite("==");
                break;
            case greaterEqualToken:
                prettyWrite(">=");
                break;
            case lessEqualToken:
                prettyWrite("<=");
                break;
            case notEqualToken:
                prettyWrite("!=");
                break;
            default:
                break;
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eval'");
    }

}
