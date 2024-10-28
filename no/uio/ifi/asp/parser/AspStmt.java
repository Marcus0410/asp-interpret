// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

abstract class AspStmt extends AspSyntax {
    AspStmt(int n) {
        super(n);
    }

    static AspStmt parse(Scanner s) {
        enterParser("stmt");

        AspStmt as = null;
        if (s.curToken().kind == forToken || s.curToken().kind == ifToken || s.curToken().kind == whileToken
                || s.curToken().kind == defToken)
            as = AspCompoundStmt.parse(s);
        else
            as = AspSmallStmtList.parse(s);

        leaveParser("stmt");
        return as;
    }
}
