package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

abstract class AspCompoundStmt extends AspStmt {

    AspCompoundStmt(int n) {
        super(n);
    }

    static AspCompoundStmt parse(Scanner s) {
        enterParser("compound stmt");
        AspCompoundStmt acs = null;

        if (s.curToken().kind == forToken)
            acs = AspForStmt.parse(s);
        else if (s.curToken().kind == ifToken)
            acs = AspIfStmt.parse(s);
        else if (s.curToken().kind == whileToken)
            acs = AspWhileStmt.parse(s);
        else if (s.curToken().kind == defToken)
            acs = AspFuncDef.parse(s);
        else {
            parserError("Could not find compound stmt type with token: " + s.curToken(), s.curLineNum());
        }

        leaveParser("compound stmt");
        return acs;
    }

}
