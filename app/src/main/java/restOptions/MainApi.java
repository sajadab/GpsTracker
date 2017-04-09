package restOptions;

import model.HeartBeatRequestBody;
import model.HeartBeatResponse;
import model.LoginRequestBody;
import model.LoginResponse;
import model.RegisterRequestBody;
import model.RegisterResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by SAJJAD on 10/13/2015.
 */
public interface MainApi {

    @POST("/login")
    public void setLogin(@Body LoginRequestBody loginBody, Callback<LoginResponse> response);

    @POST("/register")
    public void setRegister(@Body RegisterRequestBody registerBody, Callback<RegisterResponse> response);

    @POST("/heartbeat")
    public void setHeartBeat(@Body HeartBeatRequestBody heartBeatBody, Callback<HeartBeatResponse> response);

}
