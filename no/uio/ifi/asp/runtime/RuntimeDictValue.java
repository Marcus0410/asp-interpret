package no.uio.ifi.asp.runtime;

import java.util.HashMap;
import java.util.Map;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeDictValue extends RuntimeValue {
    HashMap<String, RuntimeValue> map = new HashMap<>();

    public RuntimeDictValue(HashMap<String, RuntimeValue> hMap) {
        map = hMap;
    }

    @Override
    String typeName() {
        return "dict";
    }

    @Override
    public String toString() {
        // if list is empty
        if (map.size() == 0)
            return "{}";

        String str = "{";
        for (Map.Entry<String, RuntimeValue> e : map.entrySet()) {
            str += "\"" + e.getKey() + "\"" + ": " + e.getValue().showInfo() + ", ";
        }

        // remove trailing ", "
        str = str.substring(0, str.length() - 2);

        return str + "}";
    }

    @Override
    public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return map.get(v.getStringValue("string", where));
        }
        runtimeError("Subscription '[...]' undefined for " + v.typeName() + "!", where);
        return null;
    }

    @Override
    public RuntimeValue evalLen(AspSyntax where) {
        return new RuntimeIntegerValue(map.size());
    }

    @Override
    public RuntimeValue evalNot(AspSyntax where) {
        if (map.isEmpty()) {
            return new RuntimeBoolValue(true);
        } else {
            return new RuntimeBoolValue(false);
        }
    }

    @Override
    public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
        if (inx instanceof RuntimeStringValue) {
            map.put(inx.getStringValue("dict assign element", where), val);
        } else {
            runtimeError("Key has to be a string, but found " + inx.typeName(), where);
        }
    }
}
