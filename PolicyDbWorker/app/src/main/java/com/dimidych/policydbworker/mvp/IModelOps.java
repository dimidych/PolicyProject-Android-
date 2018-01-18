package com.dimidych.policydbworker.mvp;

public interface IModelOps {
    void onDestroy();
    void onSetLog(String message, String eventName, long documentId);
}
