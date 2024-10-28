package no.uio.ifi.asp.runtime;

import java.util.ArrayList;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeListValue extends RuntimeValue {
    ArrayList<RuntimeValue> list = new ArrayList<>();

    public RuntimeListValue(ArrayList<RuntimeValue> l) {
        list = l;
    }

    @Override
    String typeName() {
        return "list";
    }

    public ArrayList<RuntimeValue> getList(String what, AspSyntax where) {
        return list;
    }

    @Override
    public String toString() {
        // if list is empty
        if (list.size() == 0)
            return "[]";

        String str = "[";
        for (RuntimeValue expr : list) {
            str += expr.showInfo() + ", ";
        }

        // remove trailing ", "
        str = str.substring(0, str.length() - 2);

        return str + "]";
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
        if (list.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue) {
            long vInt = v.getIntValue("integer", where);

            ArrayList<RuntimeValue> newList = new ArrayList<>();

            // add all elements of the original list vInt times
            for (int i = 0; i < vInt; i++) {
                for (RuntimeValue expr : list) {
                    newList.add(expr);
                }
            }
            return new RuntimeListValue(newList);
        }
        runtimeError("'*' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue) {
            return list.get((int) v.getIntValue("integer", where));
        }
        runtimeError("Subscription '[...]' undefined for " + v.typeName() + "!", where);
        return null;
    }

    @Override
    public RuntimeValue evalLen(AspSyntax where) {
        return new RuntimeIntegerValue(list.size());
    }

    @Override
    public RuntimeValue evalNot(AspSyntax where) {
        return new RuntimeBoolValue(!getBoolValue("not operand", where));
    }

    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeNoneValue) {
            return new RuntimeBoolValue(false);
        }
        runtimeError("'==' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
        if (inx instanceof RuntimeIntegerValue) {
            list.set((int) inx.getIntValue("list assign element", where), val);
        } else {
            runtimeError("Index has to be an integer, but found " + inx.typeName(), where);
        }
    }
}
