package com.dimidych.policyservicestarter.mvp;

//v->p
public interface IPresenterOps {
    void onConfigurationChanged(IViewRequiredOps view);
    void onDestroy(boolean isChangingConfig);
}
