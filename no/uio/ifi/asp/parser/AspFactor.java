package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import no.uio.ifi.asp.scanner.TokenKind;

import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspFactor extends AspSyntax {
    ArrayList<AspFactorPrefix> prefixs = new ArrayList<>();
    ArrayList<AspPrimary> primaries = new ArrayList<>();
    ArrayList<AspFactorOpr> oprs = new ArrayList<>();

    AspFactor(int n) {
        super(n);
    }

    static AspFactor parse(Scanner s) {
        enterParser("factor");

        AspFactor af = new AspFactor(s.curLineNum());

        while (true) {
            // add prefix if it exists
            if (s.isFactorPrefix())
                af.prefixs.add(AspFactorPrefix.parse(s));
            // add null to prefix if not
            else
                af.prefixs.add(null);

            af.primaries.add(AspPrimary.parse(s));

            if (s.isFactorOpr()) {
                af.oprs.add(AspFactorOpr.parse(s));
            } else {
                // no factor opr, therefore finished
                break;
            }
        }

        leaveParser("factor");
        return af;
    }

    @Override
    void prettyPrint() {
        for (int i = 0; i < primaries.size(); i++) {
            if (prefixs.get(i) != null)
                prefixs.get(i).prettyPrint();

            primaries.get(i).prettyPrint();

            if (oprs.size() > i) {
                prettyWrite(" ");
                oprs.get(i).prettyPrint();
                prettyWrite(" ");

            }
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = primaries.get(0).eval(curScope);
        // look for negative prefix
        if (prefixs.get(0) != null && prefixs.get(0).prefix.kind == minusToken)
            v = v.evalNegate(this);

        for (int i = 0; i < oprs.size(); i++) {
            TokenKind oprKind = oprs.get(i).opr.kind;

            // get new primary
            RuntimeValue primary = primaries.get(i + 1).eval(curScope);
            // look for minus prefix
            if (prefixs.get(i + 1) != null && prefixs.get(i + 1).prefix.kind == minusToken)
                primary = primary.evalNegate(this);

            // multiply
            if (oprKind == astToken) {
                v = v.evalMultiply(primary, this);
            }
            // divide
            if (oprKind == slashToken) {
                v = v.evalDivide(primary, this);
            }
            // modulo
            if (oprKind == percentToken) {
                v = v.evalModulo(primary, this);
            }
            // int divide
            if (oprKind == doubleSlashToken) {
                v = v.evalIntDivide(primary, this);
            }
        }
        return v;
    }

}
