package com.dimidych.policyservicestarter.mvp;

import com.dimidych.policydbworker.Result;
import com.dimidych.policydbworker.UserDataContract;

import java.util.Map;

//v->p
public interface INetworkPresenterOps extends IPresenterOps{
    boolean ping(String ipAddress);
    Result<UserDataContract[]> testUserService(String ipAddress, String port, String serviceName, UserDataContract param);
    Map.Entry<String, String> getConnectionSettings();
    boolean updateConnectionSettings(String ipAddress, String port);
}
