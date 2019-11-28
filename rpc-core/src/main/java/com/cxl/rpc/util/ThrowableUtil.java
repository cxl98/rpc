package com.cxl.rpc.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThrowableUtil {
    /**
     * parse error to String
     *
     */
    public static String toString(Throwable e){
        StringWriter stringWriter=new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String errorMsg=stringWriter.toString();
        return errorMsg;
    }
}
