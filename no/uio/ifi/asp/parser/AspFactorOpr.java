package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.Token;
import no.uio.ifi.asp.scanner.TokenKind;

public class AspFactorOpr extends AspSyntax {
    Token opr;

    AspFactorOpr(int n) {
        super(n);
    }

    static AspFactorOpr parse(Scanner s) {
        enterParser("factor opr");

        AspFactorOpr afp = new AspFactorOpr(s.curLineNum());
        afp.opr = s.curToken();
        skip(s, afp.opr.kind);

        leaveParser("factor opr");
        return afp;
    }

    void prettyPrint() {
        TokenKind k = opr.kind;
        switch (k) {
            case astToken:
                prettyWrite("*");
                break;
            case slashToken:
                prettyWrite("/");
                break;
            case percentToken:
                prettyWrite("%");
                break;
            case doubleSlashToken:
                prettyWrite("//");
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
