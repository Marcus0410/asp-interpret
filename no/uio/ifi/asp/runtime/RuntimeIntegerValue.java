package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeIntegerValue extends RuntimeValue {
    long integerValue;

    public RuntimeIntegerValue(long n) {
        integerValue = n;
    }

    @Override
    String typeName() {
        return "integer";
    }

    @Override
    public String toString() {
        return Long.toString(integerValue);
    }

    @Override
    public long getIntValue(String what, AspSyntax where) {
        return integerValue;
    }

    @Override
    public String getStringValue(String what, AspSyntax where) {
        return "" + integerValue;
    }

    @Override
    public double getFloatValue(String what, AspSyntax where) {
        return (double) integerValue;
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
        if (integerValue == 0)
            return false;

        return true;

    }

    @Override
    public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue) {
            return new RuntimeIntegerValue(integerValue + v.getIntValue("integer", where));
        } else if (v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(integerValue + v.getFloatValue("float", where));
        }
        runtimeError("Type error for +.", where);
        return null;
    }

    @Override
    public RuntimeValue evalSubtract(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue) {
            return new RuntimeIntegerValue(integerValue - v.getIntValue("integer", where));
        } else if (v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(integerValue - v.getFloatValue("float", where));
        }
        runtimeError("Type error for +.", where);
        return null;
    }

    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue) {
            if (integerValue == v.getIntValue("integer", where))
                return new RuntimeBoolValue(true);
            else
                return new RuntimeBoolValue(false);
        } else if (v instanceof RuntimeFloatValue) {
            if (integerValue == v.getFloatValue("float", where))
                return new RuntimeBoolValue(true);
            else
                return new RuntimeBoolValue(false);

        }
        runtimeError("Type error for ==.", where);
        return null;
    }

    @Override
    public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue) {
            if (getIntValue("float", where) != v.getIntValue("integer", where))
                return new RuntimeBoolValue(true);
            else
                return new RuntimeBoolValue(false);
        }
        if (v instanceof RuntimeFloatValue) {
            if (integerValue != v.getIntValue("integer", where))
                return new RuntimeBoolValue(true);
            else
                return new RuntimeBoolValue(false);
        }
        runtimeError("Type error for !=.", where);
        return null;
    }

    @Override
    public RuntimeValue evalNegate(AspSyntax where) {
        return new RuntimeIntegerValue(-integerValue);
    }

    @Override
    public RuntimeValue evalPositive(AspSyntax where) {
        return new RuntimeIntegerValue(integerValue);
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue) {
            return new RuntimeIntegerValue(integerValue * v.getIntValue("integer", where));
        } else if (v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(integerValue * v.getFloatValue("float", where));
        }
        runtimeError("'*' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalDivide(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue || v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(integerValue / v.getFloatValue("float", where));
        }
        runtimeError("'/' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalIntDivide(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue) {
            long l = Math.floorDiv(integerValue, v.getIntValue("integer", where));
            return new RuntimeIntegerValue(l);
        } else if (v instanceof RuntimeFloatValue) {
            double d = Math.floor(integerValue / v.getFloatValue("float", where));
            return new RuntimeFloatValue(d);
        }
        runtimeError("'//' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeFloatValue || v instanceof RuntimeIntegerValue) {
            boolean bool = integerValue < v.getFloatValue("term", where);
            return new RuntimeBoolValue(bool);
        }
        runtimeError("'<' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeFloatValue || v instanceof RuntimeIntegerValue) {
            boolean bool = integerValue > v.getFloatValue("term", where);
            return new RuntimeBoolValue(bool);
        }
        runtimeError("'>' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeFloatValue || v instanceof RuntimeIntegerValue) {
            boolean bool = integerValue <= v.getFloatValue("term", where);
            return new RuntimeBoolValue(bool);
        }
        runtimeError("'<=' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeFloatValue || v instanceof RuntimeIntegerValue) {
            boolean bool = integerValue >= v.getFloatValue("term", where);
            return new RuntimeBoolValue(bool);
        }
        runtimeError("'>=' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    public RuntimeValue evalModulo(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue) {
            return new RuntimeIntegerValue(Math.floorMod(integerValue, v.getIntValue("integer", where)));
        } else if (v instanceof RuntimeFloatValue) {
            double vFloat = v.getFloatValue("float", where);
            return new RuntimeFloatValue(integerValue - vFloat * Math.floor(integerValue / vFloat));
        }
        runtimeError("'%' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalNot(AspSyntax where) {
        return new RuntimeBoolValue(!getBoolValue("not operand", where));

    }
}
