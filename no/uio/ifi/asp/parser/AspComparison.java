package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.runtime.RuntimeBoolValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;

import static no.uio.ifi.asp.scanner.TokenKind.*;

class AspComparison extends AspSyntax {
    ArrayList<AspTerm> terms = new ArrayList<>();
    ArrayList<AspCompOpr> compOprs = new ArrayList<>();

    AspComparison(int n) {
        super(n);
    }

    static AspComparison parse(Scanner s) {
        enterParser("comparison");

        AspComparison ac = new AspComparison(s.curLineNum());

        while (true) {
            ac.terms.add(AspTerm.parse(s));

            if (!s.isCompOpr()) {
                break;
            }
            ac.compOprs.add(AspCompOpr.parse(s));
        }

        leaveParser("comparison");
        return ac;
    }

    @Override
    void prettyPrint() {
        for (int i = 0; i < terms.size(); i++) {
            terms.get(i).prettyPrint();

            if (compOprs.size() > i) {
                prettyWrite(" ");
                compOprs.get(i).prettyPrint();
                prettyWrite(" ");
            }
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        // if only one term
        if (terms.size() == 1) {
            return terms.get(0).eval(curScope);
        }

        boolean returnValue = true;
        for (int i = 0; i < terms.size() - 1; i++) {
            RuntimeValue v1 = terms.get(i).eval(curScope);
            RuntimeValue v2 = terms.get(i + 1).eval(curScope);

            TokenKind oprKind = compOprs.get(i).opr.kind;

            // ==
            if (oprKind == doubleEqualToken) {
                if (v1.evalEqual(v2, this).toString().equals("False")) {
                    returnValue = false;
                    break;
                }
            }
            // <
            if (oprKind == lessToken) {
                if (v1.evalLess(v2, this).toString().equals("False")) {
                    returnValue = false;
                    break;
                }
            }
            // >
            if (oprKind == greaterToken) {
                if (v1.evalGreater(v2, this).toString().equals("False")) {
                    returnValue = false;
                    break;
                }
            }
            // <=
            if (oprKind == lessEqualToken) {
                if (v1.evalLessEqual(v2, this).toString().equals("False")) {
                    returnValue = false;
                    break;
                }
            }
            // >=
            if (oprKind == greaterEqualToken) {
                if (v1.evalGreaterEqual(v2, this).toString().equals("False")) {
                    returnValue = false;
                    break;
                }
            }
            // !=
            if (oprKind == notEqualToken) {
                if (v1.evalNotEqual(v2, this).toString().equals("False")) {
                    returnValue = false;
                    break;
                }
            }
        }
        return new RuntimeBoolValue(returnValue);
    }
}
