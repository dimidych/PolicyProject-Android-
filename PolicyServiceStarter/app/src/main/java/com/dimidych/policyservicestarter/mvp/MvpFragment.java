package com.dimidych.policyservicestarter.mvp;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public abstract class MvpFragment extends Fragment implements IViewRequiredOps{
    public final StateMaintainer _stateMaintainer;
    private String _fragmentId;

    public MvpFragment(String fragmentId){_stateMaintainer=null;}

    public MvpFragment(FragmentManager fragmentManager, String fragmentId){
        _stateMaintainer = new StateMaintainer(fragmentManager, fragmentId);
        _fragmentId=fragmentId;
    }

    @Override
    public void showToast(String msg) {
        Snackbar.make(this.getView(), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void showAlert(String msg) {
        Snackbar.make(this.getView(), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected abstract void initialize(IViewRequiredOps view)
            throws InstantiationException, IllegalAccessException;

    protected abstract void reinitialize(IViewRequiredOps view)
            throws InstantiationException, IllegalAccessException;

    public void startMVPOps() {
        try {
            if (_stateMaintainer.firstTimeIn()) {
                Log.d(_fragmentId, "onCreate() called for the first time");
                initialize(this);
            } else {
                Log.d(_fragmentId, "onCreate() called more than once");
                reinitialize(this);
            }
        } catch (Exception e) {
            Log.d(_fragmentId, "onCreate() " + e);
            throw new RuntimeException(e);
        }
    }
}
