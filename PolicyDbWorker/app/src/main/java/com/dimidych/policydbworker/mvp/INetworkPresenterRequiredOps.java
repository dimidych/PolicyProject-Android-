package com.dimidych.policydbworker.mvp;

//m->p
public interface INetworkPresenterRequiredOps extends IPresenterRequiredOps {
    void onUpdateConnectionSettings(String ipAddress, String port);
}
