package com.dimidych.policyservicestarter.mvp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.dimidych.policydbworker.DbWorker;
import com.dimidych.policydbworker.GetLoginInfoAsyncTaskLoader;
import com.dimidych.policydbworker.mvp.IGetCertModelOps;
import com.dimidych.policydbworker.mvp.IGetCertPresenterRequiredOps;

import java.lang.ref.WeakReference;

public class GetCertPresenter extends AsyncTaskLoader<String>
        implements IGetCertPresenterOps, IGetCertPresenterRequiredOps {
    private WeakReference<IViewRequiredOps> _view;
    private IGetCertModelOps _model;
    private static final String LOG_TAG = "GetCertPresenter";

    public GetCertPresenter(IViewRequiredOps view, Context context) {
        super(context);
        _view = new WeakReference<>(view);
        _model = new DbWorker(context, this);
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

    @Override
    public String getCertificateFromServer() {
        GetLoginInfoAsyncTaskLoader certificateLoader = new GetLoginInfoAsyncTaskLoader((DbWorker)_model);
        String cert = certificateLoader.loadInBackground().SomeResult.getValue();
        _model.updateCertificate(cert);
        return cert;
    }

    @Override
    public void onUpdateCertificate(String s) {
        Log.d(LOG_TAG, "Certificate updated");
        _view.get().showToast("Certificate updated");
    }

    @Override
    public void onError(String errorMsg) {
        Log.d(LOG_TAG, errorMsg);
        _view.get().showAlert(errorMsg);
    }

    @Override
    public String loadInBackground() {
        return getCertificateFromServer();
    }
}
