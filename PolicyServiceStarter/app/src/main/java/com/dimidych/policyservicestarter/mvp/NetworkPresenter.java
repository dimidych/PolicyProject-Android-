package com.dimidych.policyservicestarter.mvp;

import android.content.Context;
import android.util.Log;

import com.dimidych.policydbworker.DbWorker;
import com.dimidych.policydbworker.NetworkWorker;
import com.dimidych.policydbworker.Result;
import com.dimidych.policydbworker.UserDataContract;
import com.dimidych.policydbworker.mvp.INetworkModelOps;
import com.dimidych.policydbworker.mvp.INetworkPresenterRequiredOps;

import java.lang.ref.WeakReference;
import java.util.Map;

public class NetworkPresenter implements INetworkPresenterOps, INetworkPresenterRequiredOps {
    private WeakReference<IViewRequiredOps> _view;
    private INetworkModelOps _model;
    private String LOG_TAG = getClass().getSimpleName();

    public NetworkPresenter(IViewRequiredOps view, Context ctx) {
        _view = new WeakReference<>(view);
        _model = new DbWorker(ctx, this);
    }

    @Override
    public boolean ping(String ipAddress) {
        return NetworkWorker.ping(ipAddress);
    }

    @Override
    public Result<UserDataContract[]> testUserService(String ipAddress, String port, String serviceName, UserDataContract userDataContract) {
        return NetworkWorker.commonServiceCall(ipAddress, port, serviceName, userDataContract);
    }

    @Override
    public Map.Entry<String, String> getConnectionSettings() {
        return _model.getConnectionSettings();
    }

    @Override
    public boolean updateConnectionSettings(String ipAddress, String port) {
        return _model.updateConnectionSettings(ipAddress, port);
    }

    @Override
    public void onUpdateConnectionSettings(String s, String s1) {
        Log.d(LOG_TAG, "Network settings updated");
        _view.get().showToast("Network settings updated");
    }

    @Override
    public void onError(String errorMsg) {
        Log.d(LOG_TAG, errorMsg);
        _view.get().showAlert(errorMsg);
    }

    @Override
    public void onConfigurationChanged(IViewRequiredOps view) {
        _view = new WeakReference<>(view);
    }

    @Override
    public void onDestroy(boolean isChangingConfig) {
        _view = null;

        if (!isChangingConfig)
            _model.onDestroy();
    }

    @Override
    public void onSetEventLog(String message, String eventName, long documentId) {
        _model.onSetLog(message,eventName,documentId);
    }
}
