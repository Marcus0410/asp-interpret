package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

import java.util.ArrayList;

public class AspIfStmt extends AspCompoundStmt {
    ArrayList<AspExpr> exprs = new ArrayList<>();
    ArrayList<AspSuite> suites = new ArrayList<>();
    AspSuite elseSuite;

    AspIfStmt(int n) {
        super(n);
    }

    static AspIfStmt parse(Scanner s) {
        enterParser("if stmt");

        AspIfStmt aif = new AspIfStmt(s.curLineNum());

        skip(s, ifToken);
        // add first if check expr
        aif.exprs.add(AspExpr.parse(s));
        skip(s, colonToken);
        aif.suites.add(AspSuite.parse(s));
        // if elif statements, add them
        while (s.curToken().kind == elifToken) {
            skip(s, elifToken);
            aif.exprs.add(AspExpr.parse(s));
            skip(s, colonToken);
            aif.suites.add(AspSuite.parse(s));
        }

        if (s.curToken().kind == elseToken) {
            skip(s, elseToken);
            skip(s, colonToken);
            aif.elseSuite = AspSuite.parse(s);
        }

        leaveParser("if stmt");
        return aif;
    }

    @Override
    void prettyPrint() {
        prettyWrite("if ");

        // write if and elif
        for (int i = 0; i < exprs.size(); i++) {
            if (i > 0)
                prettyWrite("elif ");
            exprs.get(i).prettyPrint();
            prettyWrite(":");
            suites.get(i).prettyPrint();
        }
        // write else
        if (elseSuite != null) {
            prettyWrite("else:");
            elseSuite.prettyPrint();
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = null;
        boolean runElse = true;
        for (int i = 0; i < exprs.size(); i++) {
            v = exprs.get(i).eval(curScope);
            if (v.getBoolValue("if stmt", this)) {
                trace("if True alt #" + (i + 1) + ": ...");
                suites.get(i).eval(curScope);
                runElse = false;
                break;
            }
        }

        if (runElse && elseSuite != null) {
            trace("else: ...");
            elseSuite.eval(curScope);
        }
        return null;
    }

}
