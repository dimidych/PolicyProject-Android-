package com.dimidych.policyservicestarter.mvp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dimidych.policydbworker.mvp.IPresenterRequiredOps;
import com.dimidych.policyservicestarter.PolicyService;

import java.lang.ref.WeakReference;

public class ServicePresenter implements IServicePresenterOps, IPresenterRequiredOps {
    private WeakReference<IViewRequiredOps> _view;
    private Context _context;
    private String LOG_TAG = getClass().getSimpleName();
    //private DbWorker _model;

    public ServicePresenter(IViewRequiredOps view, Context context){
        _context = context;
        _view = new WeakReference<>(view);
        //_model=new DbWorker(context);
    }

    @Override
    public void onConfigurationChanged(IViewRequiredOps view) {
        _view = new WeakReference<>(view);
    }

    @Override
    public void onDestroy(boolean isChangingConfig) {
        _view = null;
    }

    @Override
    public void onSetEventLog(String message, String eventName, long documentId) {}

    @Override
    public void onError(String errorMsg) {
        Log.d(LOG_TAG, errorMsg);
        _view.get().showAlert(errorMsg);
    }

    @Override
    public String startService() {
        _context.startService(new Intent(_context, PolicyService.class));
        return "Сервис запущен";
    }

    @Override
    public String stopService() {
        _context.stopService(new Intent(_context, PolicyService.class));
        return "Сервис остановлен";
    }
}
