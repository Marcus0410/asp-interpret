// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.runtime;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.NoSuchElementException;

import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeLibrary extends RuntimeScope {
    private Scanner keyboard = new Scanner(System.in);

    public RuntimeLibrary() {
        assign("float", new RuntimeFunc("float") {
            @Override
            public RuntimeValue evalFuncCall(
                    ArrayList<RuntimeValue> actualParams,
                    AspSyntax where) {
                checkNumParams(actualParams, 1, "float", where);
                return new RuntimeFloatValue(actualParams.get(0).getFloatValue("argument", where));
            }
        });
        assign("input", new RuntimeFunc("input") {
            @Override
            public RuntimeValue evalFuncCall(
                    ArrayList<RuntimeValue> actualParams,
                    AspSyntax where) {
                checkNumParams(actualParams, 1, "input", where);
                System.out.print(actualParams.get(0).toString()); // write to terminal
                return new RuntimeStringValue(keyboard.nextLine());
            }
        });
        assign("int", new RuntimeFunc("int") {
            @Override
            public RuntimeValue evalFuncCall(
                    ArrayList<RuntimeValue> actualParams,
                    AspSyntax where) {
                checkNumParams(actualParams, 1, "int", where);
                return new RuntimeIntegerValue(actualParams.get(0).getIntValue("argument", where));
            }
        });
        assign("len", new RuntimeFunc("len") {
            @Override
            public RuntimeValue evalFuncCall(
                    ArrayList<RuntimeValue> actualParams,
                    AspSyntax where) {
                checkNumParams(actualParams, 1, "len", where);
                RuntimeValue runtimeInt = actualParams.get(0).evalLen(where);
                return runtimeInt;
            }
        });
        assign("print", new RuntimeFunc("print") {
            @Override
            public RuntimeValue evalFuncCall(
                    ArrayList<RuntimeValue> actualParams,
                    AspSyntax where) {
                checkNumParams(actualParams, actualParams.size(), "print", where);

                String str = "";
                // write to terminal
                for (RuntimeValue v : actualParams) {
                    str += v.toString() + " ";
                }

                System.out.println(str);

                return new RuntimeNoneValue();
            }
        });
        assign("range", new RuntimeFunc("range") {
            @Override
            public RuntimeValue evalFuncCall(
                    ArrayList<RuntimeValue> actualParams,
                    AspSyntax where) {
                checkNumParams(actualParams, 2, "range", where);

                ArrayList<RuntimeValue> list = new ArrayList<>();
                int first = (int) actualParams.get(0).getIntValue("first argument", where);
                int last = (int) actualParams.get(1).getIntValue("second argument", where);

                for (int i = first; i < last; i++) {
                    list.add(new RuntimeIntegerValue(i));
                }

                return new RuntimeListValue(list);
            }
        });
        assign("str", new RuntimeFunc("str") {
            @Override
            public RuntimeValue evalFuncCall(
                    ArrayList<RuntimeValue> actualParams,
                    AspSyntax where) {
                checkNumParams(actualParams, 1, "str", where);
                return new RuntimeStringValue(actualParams.get(0).getStringValue("argument", where));
            }
        });
    }

    private void checkNumParams(ArrayList<RuntimeValue> actArgs,
            int nCorrect, String id, AspSyntax where) {
        if (actArgs.size() != nCorrect)
            RuntimeValue.runtimeError("Wrong number of parameters to " + id + "!", where);
    }
}
