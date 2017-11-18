package com.dimidych.policydbworker.mvp;

import java.util.Map;

//p->m
public interface INetworkModelOps extends IModelOps {
    Map.Entry<String, String> getConnectionSettings();
    boolean updateConnectionSettings(String ipAddress, String port);
}
