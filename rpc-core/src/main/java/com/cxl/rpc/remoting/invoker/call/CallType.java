package com.cxl.rpc.remoting.invoker.call;

public enum  CallType {
    SYNC,

    FUTURE,

    CALLBACK,

    ONEWAY;

    public static CallType match(String name,CallType defsultCallTyp){
        for (CallType item: CallType.values()) {
            if (item.name().equals(name)){
                return item;
            }
        }
        return defsultCallTyp;
    }
}
