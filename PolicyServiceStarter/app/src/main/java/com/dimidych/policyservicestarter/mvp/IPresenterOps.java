package com.dimidych.policyservicestarter.mvp;

//v->p
public interface IPresenterOps {
    void onConfigurationChanged(IViewRequiredOps view);
    void onDestroy(boolean isChangingConfig);
    void onSetEventLog(String message, String eventName, long documentId);
}
