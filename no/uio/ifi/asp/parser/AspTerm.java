package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspTerm extends AspSyntax {
    ArrayList<AspFactor> factors = new ArrayList<>();
    ArrayList<AspTermOpr> termOprs = new ArrayList<>();

    AspTerm(int n) {
        super(n);
    }

    static AspTerm parse(Scanner s) {
        enterParser("term");

        AspTerm at = new AspTerm(s.curLineNum());
        while (true) {
            at.factors.add(AspFactor.parse(s));

            if (!s.isTermOpr()) {
                break;
            }

            at.termOprs.add(AspTermOpr.parse(s));
        }

        leaveParser("term");
        return at;
    }

    @Override
    void prettyPrint() {
        for (int i = 0; i < factors.size(); i++) {
            factors.get(i).prettyPrint();

            if (termOprs.size() > i)
                termOprs.get(i).prettyPrint();
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = factors.get(0).eval(curScope);
        for (int i = 0; i < termOprs.size(); i++) {
            // if +
            if (termOprs.get(i).opr.kind == plusToken) {
                v = v.evalAdd(factors.get(i + 1).eval(curScope), this);
            }
            // if -
            else {
                v = v.evalSubtract(factors.get(i + 1).eval(curScope), this);
            }
        }
        return v;
    }

}
