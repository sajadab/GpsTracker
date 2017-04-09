package model;

/**
 * Created by SAJJAD on 4/7/2017.
 */
public class RegisterResponse {

    public Boolean success;

    public Integer status;

    public String message;

    public RegisterResponseData data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
