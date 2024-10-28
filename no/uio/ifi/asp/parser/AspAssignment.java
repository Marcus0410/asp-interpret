package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.RuntimeDictValue;
import no.uio.ifi.asp.runtime.RuntimeListValue;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspAssignment extends AspSmallStmt {
    ArrayList<AspSubscription> subscriptions = new ArrayList<>();
    AspName name;
    AspExpr expr;

    AspAssignment(int n) {
        super(n);
    }

    static AspAssignment parse(Scanner s) {
        enterParser("assignment");

        AspAssignment aa = new AspAssignment(s.curLineNum());

        // first add name
        aa.name = AspName.parse(s);

        // add subscriptions
        while (s.curToken().kind == leftBracketToken) {
            aa.subscriptions.add(AspSubscription.parse(s));
        }

        skip(s, equalToken);

        // add expr
        aa.expr = AspExpr.parse(s);

        leaveParser("assignment");
        return aa;
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        // if no subscriptions, assign simple variable
        if (subscriptions.size() == 0) {
            RuntimeValue exprValue = expr.eval(curScope);
            trace(name.name + " = " + exprValue.showInfo());

            if (curScope.hasGlobalName(name.name)) {
                Main.globalScope.assign(name.name, exprValue);
            } else {
                curScope.assign(name.name, exprValue);
            }
        }
        // with subscriptions
        else {
            RuntimeValue collection = curScope.find(name.name, this);

            String subString = ""; // string with subscriptions for trace call

            // find last collection
            for (int i = 0; i < subscriptions.size() - 1; i++) {
                RuntimeValue index = subscriptions.get(i).eval(curScope);
                collection = collection.evalSubscription(index, this);
                subString += "[" + index.showInfo() + "]";
            }
            // find last subscription
            RuntimeValue index = subscriptions.get(subscriptions.size() - 1).eval(curScope);
            collection.evalAssignElem(index, expr.eval(curScope), this);

            subString += "[" + index.showInfo() + "]"; // add last subscription
            trace(name.name + subString + " = " + expr.eval(curScope).showInfo());
        }

        return null;
    }

    @Override
    void prettyPrint() {
        name.prettyPrint();

        for (AspSubscription sub : subscriptions) {
            sub.prettyPrint();
        }
        prettyWrite(" = ");
        expr.prettyPrint();
    }
}
