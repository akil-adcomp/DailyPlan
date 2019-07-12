package com.zoheb.dailyplan.Retrofit;

public class ResponseFormat {

    private String status;
    private String failedReason;
    private int failedValue;
    private String parameter;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public int getFailedValue() {
        return failedValue;
    }

    public void setFailedValue(int failedValue) {
        this.failedValue = failedValue;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "ResponseFormat{" +
                "status='" + status + '\'' +
                ", failedReason='" + failedReason + '\'' +
                ", failedValue=" + failedValue +
                ", parameter='" + parameter + '\'' +
                '}';
    }
}
