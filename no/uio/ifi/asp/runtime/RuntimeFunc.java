package no.uio.ifi.asp.runtime;

import java.util.ArrayList;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.parser.AspName;
import no.uio.ifi.asp.parser.AspSuite;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeFunc extends RuntimeValue {
    ArrayList<AspName> formalParameters = new ArrayList<>();
    ArrayList<RuntimeValue> actualParameters = new ArrayList<>();
    AspSuite suite;
    RuntimeScope scope;
    RuntimeScope outerScope;
    String name;

    public RuntimeFunc(String funcName, ArrayList<AspName> formalParams, AspSuite funcSuite, RuntimeScope curScope) {
        formalParameters = formalParams;
        suite = funcSuite;
        outerScope = curScope;
        name = funcName;

        curScope.assign(funcName, this);
    }

    public RuntimeFunc(String funcName) {
        name = funcName;
    }

    @Override
    String typeName() {
        return "function";
    }

    @Override
    public String showInfo() {
        return "placeholder";
    }

    @Override
    public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams,
            AspSyntax where) {
        if (formalParameters.size() != actualParams.size()) {
            runtimeError("Expected " + formalParameters.size() + " parameters, but was given " + actualParams.size(),
                    where);
        }
        scope = new RuntimeScope(outerScope);

        actualParameters = actualParams;

        // assign function parameters
        for (int i = 0; i < formalParameters.size(); i++) {
            scope.assign(formalParameters.get(i).name, actualParams.get(i));
        }

        try {
            suite.eval(scope);
        } catch (RuntimeReturnValue e) {
            return e.value;
        }

        return new RuntimeNoneValue();
    }
}
