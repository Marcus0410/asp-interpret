package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeStringValue extends RuntimeValue {
    String str;

    public RuntimeStringValue(String s) {
        str = s;
    }

    @Override
    String typeName() {
        return "string";
    }

    @Override
    public String toString() {
        return str;
    }

    @Override
    public String showInfo() {
        return "\"" + str + "\"";
    }

    @Override
    public String getStringValue(String what, AspSyntax where) {
        return str;
    }

    @Override
    public long getIntValue(String what, AspSyntax where) {
        return Long.parseLong(str);
    }

    @Override
    public double getFloatValue(String what, AspSyntax where) {
        return Double.parseDouble(str);
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
        // if empty string
        if (str.equals(""))
            return false;

        return true;
    }

    @Override
    public RuntimeValue evalLen(AspSyntax where) {
        return new RuntimeIntegerValue(str.length());
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue) {
            return new RuntimeStringValue(str.repeat((int) v.getIntValue("integer", where)));
        }
        runtimeError("'*' undefined for " + typeName() + "!", where);
        return null;
    }

    @Override
    public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeStringValue(str + v.getStringValue("string", where));
        }
        runtimeError("'+' undefined for " + typeName() + "!", where);
        return null;
    }

    @Override
    public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue)
            return new RuntimeStringValue(str.charAt((int) v.getIntValue("integer", where)) + "");

        runtimeError("Subscription '[...]' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            if (v.getStringValue("string", where).equals(str)) {
                return new RuntimeBoolValue(true);
            } else {
                return new RuntimeBoolValue(false);
            }
        }
        runtimeError("'==' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!

    }

    @Override
    public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            if (!v.getStringValue("string", where).equals(str)) {
                return new RuntimeBoolValue(true);
            } else {
                return new RuntimeBoolValue(false);
            }
        }
        runtimeError("'!=' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!

    }

    @Override
    public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            if (str.compareTo(v.getStringValue(">", where)) > 0) {
                return new RuntimeBoolValue(true);
            } else {
                return new RuntimeBoolValue(false);
            }
        }
        runtimeError("'>' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            if (str.compareTo(v.getStringValue("<", where)) < 0) {
                return new RuntimeBoolValue(true);
            } else {
                return new RuntimeBoolValue(false);
            }
        }
        runtimeError("'>' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalNot(AspSyntax where) {
        return new RuntimeBoolValue(!getBoolValue("not operand", where));
    }
}
