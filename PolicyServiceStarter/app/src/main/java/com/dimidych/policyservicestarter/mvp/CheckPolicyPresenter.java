package com.dimidych.policyservicestarter.mvp;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
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
    private WeakReference<IViewRequiredOps> _view;
    private ICheckPolicyModelOps _model;
    private boolean _isChangingConfig;
    private Context _ctx;
    private String LOG_TAG = getClass().getSimpleName();

    public CheckPolicyPresenter(IViewRequiredOps view, Context context) {
        super(context);
        _ctx = context;
        _view = new WeakReference<>(view);
        _model = new DbWorker(_ctx, this);
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
        _isChangingConfig = isChangingConfig;

        if (!isChangingConfig)
            _model.onDestroy();
    }

    @Override
    public PolicySetDataContract[] getPolicySetFromService() {
        try {
            PolicySetFromServerAtLoader serverPolicySetLoader = new PolicySetFromServerAtLoader(_ctx);
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
        return getPolicySetFromService();
    }
}
