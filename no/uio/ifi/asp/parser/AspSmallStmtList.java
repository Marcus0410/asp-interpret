package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspSmallStmtList extends AspStmt {
    ArrayList<AspSmallStmt> smallStmts = new ArrayList<>();

    AspSmallStmtList(int n) {
        super(n);
    }

    static AspSmallStmtList parse(Scanner s) {
        enterParser("small stmt list");

        AspSmallStmtList stmtList = new AspSmallStmtList(s.curLineNum());

        while (s.curToken().kind != newLineToken) {
            // add small stmt
            stmtList.smallStmts.add(AspSmallStmt.parse(s));
            // if semi colon, skip
            if (s.curToken().kind == semicolonToken) {
                skip(s, semicolonToken);
            }
        }

        skip(s, newLineToken);

        leaveParser("small stmt list");
        return stmtList;
    }

    @Override
    void prettyPrint() {
        int nPrinted = 0;

        for (AspSmallStmt smallStmt : smallStmts) {
            if (nPrinted > 0) {
                prettyWrite("; ");
            }
            smallStmt.prettyPrint();
            nPrinted++;
        }
        prettyWriteLn();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = smallStmts.get(0).eval(curScope);
        for (int i = 1; i < smallStmts.size(); i++) {
            v = smallStmts.get(i).eval(curScope);
        }
        return v;
    }

}
