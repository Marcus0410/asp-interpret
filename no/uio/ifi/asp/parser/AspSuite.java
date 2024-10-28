package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeNoneValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspSuite extends AspSyntax {
    AspSmallStmtList smallStmtList;
    ArrayList<AspStmt> stmts = new ArrayList<>();

    AspSuite(int n) {
        super(n);
    }

    static AspSuite parse(Scanner s) {
        enterParser("suite");

        AspSuite as = new AspSuite(s.curLineNum());
        // if not a small stmt list
        if (s.curToken().kind == newLineToken) {
            skip(s, newLineToken);
            skip(s, indentToken);

            while (s.curToken().kind != dedentToken) {
                as.stmts.add(AspStmt.parse(s));
            }

            skip(s, dedentToken);
        } else {
            as.smallStmtList = AspSmallStmtList.parse(s);
        }

        leaveParser("suite");
        return as;
    }

    @Override
    void prettyPrint() {
        if (smallStmtList == null) {
            prettyWriteLn();
            prettyIndent();
            for (AspStmt s : stmts) {
                s.prettyPrint();
            }
            prettyDedent();
        } else {
            smallStmtList.prettyPrint();
        }
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        // if suite has a small stmt list
        if (smallStmtList != null) {
            smallStmtList.eval(curScope);
            return null;
        }

        for (AspStmt stmt : stmts) {
            stmt.eval(curScope);
        }

        return null;
    }

}
