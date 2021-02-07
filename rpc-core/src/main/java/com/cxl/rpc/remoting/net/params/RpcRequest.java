package com.cxl.rpc.remoting.net.params;

import java.io.Serializable;

/**
 * request
 */
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 420L;


    private String requestId;
    private long createMillisTime;
    private String accessToken;

    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    private String version;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getCreateMillisTime() {
        return createMillisTime;
    }

    public void setCreateMillisTime(long createMillisTime) {
        this.createMillisTime = createMillisTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes.clone();
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes.clone();
    }

    /**
     * parameters.clone()使用clone()是防止对象对修改
     * @param parameters 参数列表
     */
    public void setParameters(Object[] parameters) {
        this.parameters = parameters.clone();
    }

    public Object[] getParameters() {
        return parameters.clone();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

//    @Override
//    public String toString() {
//        return "RpcRequest{" +
//                "requestId='" + requestId + '\'' +
//                ", createMillisTime=" + createMillisTime +
//                ", accessToken='" + accessToken + '\'' +
//                ", className='" + className + '\'' +
//                ", methodName='" + methodName + '\'' +
//                ", parameterTypes=" + Arrays.toString(parameterTypes) +
//                ", parameters=" + Arrays.toString(parameters) +
//                ", version='" + version + '\'' +
//                '}';
//    }
}
