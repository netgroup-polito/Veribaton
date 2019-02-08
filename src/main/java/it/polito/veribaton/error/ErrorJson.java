package it.polito.veribaton.error;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ErrorJson{

    private Integer status;
    private String error;

    @JsonIgnore
    private String message;
    private String timeStamp;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ErrorJson(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timeStamp = String.valueOf(System.nanoTime());
    }
}
