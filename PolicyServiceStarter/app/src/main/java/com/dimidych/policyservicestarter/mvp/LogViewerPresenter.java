package com.dimidych.policyservicestarter.mvp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.dimidych.policydbworker.DbWorker;
import com.dimidych.policydbworker.EventLogDataContract;
import com.dimidych.policydbworker.mvp.IEventLogModelOps;
import com.dimidych.policydbworker.mvp.IEventLogPresenterRequiredOps;

import java.lang.ref.WeakReference;

public class LogViewerPresenter extends AsyncTaskLoader<EventLogDataContract[]>
        implements ILogViewerPresenterOps, IEventLogPresenterRequiredOps {
    private WeakReference<IViewRequiredOps> _view;
    private IEventLogModelOps _model;
    private String LOG_TAG = getClass().getSimpleName();
    public String FromDate;
    public String ToDate;
    public String EventName;

    public LogViewerPresenter(IViewRequiredOps view, Context context) {
        super(context);
        _view = new WeakReference<>(view);
        _model = new DbWorker(context, this);
    }

    @Override
    public EventLogDataContract[] getEventLog(String fromDate, String toDate, String eventName) {
        return _model.getEventLog(fromDate, toDate, -1, eventName);
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
        _model.onSetLog(message, eventName, documentId);
    }

    @Override
    public EventLogDataContract[] loadInBackground() {
        return getEventLog(FromDate, ToDate, EventName);
    }

    @Override
    public void onError(String errorMsg) {
        Log.d(LOG_TAG, errorMsg);
        _view.get().showAlert(errorMsg);
    }
}
