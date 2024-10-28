package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.runtime.RuntimeListValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspPrimary extends AspSyntax {
    AspAtom atom;
    ArrayList<AspPrimarySuffix> suffixs = new ArrayList<>();

    AspPrimary(int n) {
        super(n);
    }

    static AspPrimary parse(Scanner s) {
        enterParser("primary");

        AspPrimary ap = new AspPrimary(s.curLineNum());
        ap.atom = AspAtom.parse(s);
        // are there primary suffixes?
        while (s.curToken().kind == leftParToken || s.curToken().kind == leftBracketToken) {
            ap.suffixs.add(AspPrimarySuffix.parse(s));
        }

        leaveParser("primary");
        return ap;
    }

    @Override
    void prettyPrint() {
        atom.prettyPrint();

        for (AspPrimarySuffix suff : suffixs)
            suff.prettyPrint();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = atom.eval(curScope);
        boolean funcCall = false;

        // loop through suffixs
        for (AspPrimarySuffix suf : suffixs) {
            // if subscription
            if (suf instanceof AspSubscription) {
                v = v.evalSubscription(suf.eval(curScope), this);
            }
            // if argument
            if (suf instanceof AspArguments) {
                funcCall = true;
            }
        }

        // if this is a function call
        if (funcCall) {
            AspName name = (AspName) atom;
            RuntimeListValue arguments = (RuntimeListValue) suffixs.get(0).eval(curScope);
            ArrayList<RuntimeValue> params = arguments.getList("primary func call", this);
            String paramsString = "[";

            // add parameters to paramsString
            for (int i = 0; i < params.size(); i++) {
                // dont add comma if last param
                if (i == params.size() - 1) {
                    paramsString += params.get(i).showInfo();
                } else {
                    paramsString += params.get(i).showInfo() + ", ";
                }
            }

            paramsString += "]";

            trace("Call function " + name.name + " with params " + paramsString);
            v = v.evalFuncCall(params, this);
        }

        return v;
    }

}
