package com.dimidych.policyservicestarter.mvp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.dimidych.policydbworker.DbWorker;
import com.dimidych.policydbworker.PolicySetDataContract;
import com.dimidych.policydbworker.PolicySetFromServerAtLoader;
import com.dimidych.policydbworker.Result;
import com.dimidych.policydbworker.mvp.ICheckPolicyModelOps;
import com.dimidych.policydbworker.mvp.ICheckPolicyPresenterRequiredOps;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

public class CheckPolicyPresenter extends AsyncTaskLoader<PolicySetDataContract[]>
        implements ICheckPolicyPresenterOps, ICheckPolicyPresenterRequiredOps {
    public final byte PolicyFromDb = 0;
    public final byte CheckPolicy = 1;
    public byte CurrentOperation;
    private WeakReference<IViewRequiredOps> _view;
    private ICheckPolicyModelOps _model;

    private String LOG_TAG = getClass().getSimpleName();

    public CheckPolicyPresenter(IViewRequiredOps view, Context context) {
        super(context);
        _view = new WeakReference<>(view);
        _model = new DbWorker(context, this);
    }

    @Override
    public void onAddPolicySetToDb(PolicySetDataContract policySetDataContract) {
        Log.d(LOG_TAG, "Policy Set added");
        _view.get().showToast("Policy Set added");
    }

    @Override
    public void onUpdatePolicySet(PolicySetDataContract policySetDataContract, PolicySetDataContract policySetDataContract1) {
        Log.d(LOG_TAG, "Policy Set updated");
        _view.get().showToast("Policy Set updated");
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
        _model.onSetLog(message, eventName, documentId);
    }

    @Override
    public PolicySetDataContract[] getPolicySetFromService() {
        try {
            PolicySetFromServerAtLoader serverPolicySetLoader = new PolicySetFromServerAtLoader((DbWorker) _model);
            Result<ArrayList<Map.Entry<PolicySetDataContract, String>>> serverPolicySetRes = serverPolicySetLoader.loadInBackground();

            if (!serverPolicySetRes.BoolRes)
                throw new Exception(serverPolicySetRes.ErrorRes);

            if (serverPolicySetRes.SomeResult == null || serverPolicySetRes.SomeResult.size() == 0)
                return null;

            PolicySetDataContract[] result = new PolicySetDataContract[serverPolicySetRes.SomeResult.size()];

            for (int i = 0; i < serverPolicySetRes.SomeResult.size(); i++)
                result[i] = serverPolicySetRes.SomeResult.get(i).getKey();

            return result;
        } catch (Exception ex) {
            onError(ex.toString());
            return null;
        }
    }

    @Override
    public PolicySetDataContract[] loadInBackground() {
        return CurrentOperation == PolicyFromDb ? _model.getPolicySetFromDb() : getPolicySetFromService();
    }
}
