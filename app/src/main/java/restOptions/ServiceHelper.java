package restOptions;

import model.HeartBeatRequestBody;
import model.HeartBeatResponse;
import model.LoginRequestBody;
import model.LoginResponse;
import model.RegisterRequestBody;
import model.RegisterResponse;
import retrofit.Callback;
import retrofit.RestAdapter;

/**
 * Created by SAJJAD on 10/13/2015.
 */
public class ServiceHelper {
    public static final String ENDPOINT = "http://198.211.112.145:8001/api";
    MainApi service;

    public void createAdapter() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        service = restAdapter.create(MainApi.class);
    }


    public void setLogin(LoginRequestBody loginRequestBody, Callback<LoginResponse> callback){
        service.setLogin(loginRequestBody, callback);
    }

    public void setRegister(RegisterRequestBody registerRequestBody, Callback<RegisterResponse> callback){
        service.setRegister(registerRequestBody, callback);
    }

    public void setHeartBeat(HeartBeatRequestBody heartBeatRequestBody, Callback<HeartBeatResponse> callback){
        service.setHeartBeat(heartBeatRequestBody, callback);
    }

}
