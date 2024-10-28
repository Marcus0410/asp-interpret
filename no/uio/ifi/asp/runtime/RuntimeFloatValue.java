package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeFloatValue extends RuntimeValue {
    double floatValue;

    public RuntimeFloatValue(double n) {
        floatValue = n;
    }

    @Override
    String typeName() {
        return "float";
    }

    @Override
    public String toString() {
        return Double.toString(floatValue);
    }

    @Override
    public double getFloatValue(String what, AspSyntax where) {
        return floatValue;
    }

    @Override
    public long getIntValue(String what, AspSyntax where) {
        return Math.round(floatValue);
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
        if (floatValue == 0.0)
            return false;

        return true;

    }

    @Override
    public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue || v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(floatValue + v.getFloatValue("float", where));
        }
        runtimeError("'+' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalSubtract(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue || v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(floatValue - v.getFloatValue("float",
                    where));
        }
        runtimeError("Type error for +.", where);
        return null;
    }

    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue) {
            if (getIntValue("float", where) == v.getIntValue("integer", where))
                return new RuntimeBoolValue(true);
            else
                return new RuntimeBoolValue(false);
        }
        if (v instanceof RuntimeFloatValue) {
            if (floatValue == v.getFloatValue("float", where))
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
            if (floatValue != v.getFloatValue("float", where))
                return new RuntimeBoolValue(true);
            else
                return new RuntimeBoolValue(false);
        }
        runtimeError("Type error for !=.", where);
        return null;
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeIntegerValue || v instanceof RuntimeFloatValue) {
            return new RuntimeFloatValue(floatValue * v.getFloatValue("float", where));
        }
        runtimeError("'*' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalDivide(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeFloatValue || v instanceof RuntimeIntegerValue) {
            return new RuntimeFloatValue(floatValue / v.getFloatValue("float", where));
        }
        runtimeError("'/' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalIntDivide(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeFloatValue || v instanceof RuntimeIntegerValue) {
            return new RuntimeFloatValue(Math.floor(floatValue / v.getFloatValue("float", where)));
        }
        runtimeError("'//' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    public RuntimeValue evalModulo(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeFloatValue || v instanceof RuntimeIntegerValue) {
            return new RuntimeFloatValue(floatValue
                    - v.getFloatValue("float", where) * Math.floor(floatValue / v.getFloatValue("float", where)));
        }
        runtimeError("'%' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeFloatValue || v instanceof RuntimeIntegerValue) {
            return new RuntimeBoolValue(floatValue < v.getFloatValue("float", where));
        }
        runtimeError("'<' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeFloatValue || v instanceof RuntimeIntegerValue) {
            return new RuntimeBoolValue(floatValue <= v.getFloatValue("float", where));
        }
        runtimeError("'<=' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeFloatValue || v instanceof RuntimeIntegerValue) {
            return new RuntimeBoolValue(floatValue > v.getFloatValue("float", where));
        }
        runtimeError("'>' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeFloatValue || v instanceof RuntimeIntegerValue) {
            return new RuntimeBoolValue(floatValue >= v.getFloatValue("float", where));
        }
        runtimeError("'>=' undefined for " + typeName() + "!", where);
        return null; // Required by the compiler!
    }

    @Override
    public RuntimeValue evalNegate(AspSyntax where) {
        return new RuntimeFloatValue(-floatValue);
    }

    @Override
    public RuntimeValue evalPositive(AspSyntax where) {
        return new RuntimeFloatValue(floatValue);
    }

    @Override
    public RuntimeValue evalNot(AspSyntax where) {
        return new RuntimeBoolValue(!getBoolValue("not operand", where));

    }

}
