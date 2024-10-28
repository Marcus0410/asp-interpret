package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspAndTest extends AspSyntax {
    ArrayList<AspNotTest> notTests = new ArrayList<>();

    AspAndTest(int n) {
        super(n);
    }

    static AspAndTest parse(Scanner s) {
        enterParser("and test");

        AspAndTest aat = new AspAndTest(s.curLineNum());
        while (true) {
            // add not test
            aat.notTests.add(AspNotTest.parse(s));
            // stop if current token is not an and-token
            if (s.curToken().kind != andToken)
                break;
            skip(s, andToken);
        }

        leaveParser("and test");
        return aat;
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = notTests.get(0).eval(curScope);
        for (int i = 1; i < notTests.size(); i++) {
            if (!v.getBoolValue("and operand", this))
                return v;
            v = notTests.get(i).eval(curScope);
        }
        return v;
    }

    @Override
    void prettyPrint() {
        int nPrinted = 0;

        for (AspNotTest ant : notTests) {
            if (nPrinted > 0)
                prettyWrite(" and ");
            ant.prettyPrint();
            ++nPrinted;
        }
    }
}
