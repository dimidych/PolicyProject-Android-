package com.dimidych.policyservicestarter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dimidych.policydbworker.PolicySetDataContract;
import com.dimidych.policyservicestarter.mvp.CheckPolicyPresenter;
import com.dimidych.policyservicestarter.mvp.ICheckPolicyPresenterOps;
import com.dimidych.policyservicestarter.mvp.IPresenterOps;
import com.dimidych.policyservicestarter.mvp.IViewRequiredOps;
import com.dimidych.policyservicestarter.mvp.MvpFragment;

public class CheckPolicyFragment extends MvpFragment
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<PolicySetDataContract[]> {
    private static final String TAG = "CheckPolicyFragment";
    private ICheckPolicyPresenterOps _presenter;
    private TextView _txtStatus;
    private ListView _lstCheckedPolicies;
    private Button _btnCheckPolicies;
    private Button _btnPoliciesInDb;
    private PolicySetAdapter _policySetAdapter;
    public static final int LOADER_ID = 2;
    private Loader<PolicySetDataContract[]> _loader;

    public CheckPolicyFragment() {
        super(TAG);
    }

    @SuppressLint("ValidFragment")
    public CheckPolicyFragment(FragmentManager fragmentManager) {
        super(fragmentManager, TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startMVPOps();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_policy, container, false);
        _txtStatus = (TextView) view.findViewById(R.id.txtStatus);
        _lstCheckedPolicies = (ListView) view.findViewById(R.id.lstCheckedPolicies);
        _loader = getLoaderManager().initLoader(LOADER_ID, null, this);
        _btnCheckPolicies = (Button) view.findViewById(R.id.btnCheckPolicies);
        _btnCheckPolicies.setOnClickListener(this);
        _btnPoliciesInDb = (Button) view.findViewById(R.id.btnPoliciesInDb);
        _btnPoliciesInDb.setOnClickListener(this);
        return view;
    }

    @Override
    protected void initialize(IViewRequiredOps view) throws InstantiationException, IllegalAccessException {
        _presenter = new CheckPolicyPresenter(view, this.getContext());
        _stateMaintainer.put(IPresenterOps.class.getSimpleName(), _presenter);
    }

    @Override
    protected void reinitialize(IViewRequiredOps view) throws InstantiationException, IllegalAccessException {
        _presenter = _stateMaintainer.get(IPresenterOps.class.getSimpleName());

        if (_presenter == null) {
            Log.w(TAG, "recreating Presenter");
            initialize(view);
        } else {
            _presenter.onConfigurationChanged(view);
        }
    }

    @Override
    public void onClick(View view) {
        try {
            CheckPolicyPresenter presenter = (CheckPolicyPresenter) _presenter;
            presenter.CurrentOperation = view.getId() == R.id.btnCheckPolicies ? presenter.CheckPolicy : presenter.PolicyFromDb;
            _loader.onContentChanged();
        } catch (Exception ex) {
            String strErr = " Error in onClick - " + ex.getMessage();
            Log.d(TAG, strErr);
            _presenter.onSetEventLog(TAG + strErr, "Error", -1);
        }
    }

    @Override
    public Loader<PolicySetDataContract[]> onCreateLoader(int id, Bundle args) {
        try {
            if (id == LOADER_ID)
                reinitialize(this);
        } catch (Exception ex) {
            String strErr = " Error in onCreateLoader - " + ex.getMessage();
            _txtStatus.setText(strErr);
            Log.d(TAG, strErr);
            _presenter.onSetEventLog(TAG + strErr, "Error", -1);
        }

        return (Loader<PolicySetDataContract[]>) _presenter;
    }

    @Override
    public void onLoadFinished(Loader<PolicySetDataContract[]> loader, PolicySetDataContract[] data) {
        switch (loader.getId()) {
            case LOADER_ID:
                try {
                    if (data == null || data.length < 1) {
                        _txtStatus.setText("Все политики прошли проверку");
                        return;
                    }

                    _policySetAdapter = new PolicySetAdapter(getContext(), data);
                    _lstCheckedPolicies.setAdapter(_policySetAdapter);
                    _policySetAdapter.notifyDataSetChanged();
                    _txtStatus.setText("Выполнено");
                } catch (Exception ex) {
                    String strErr = " Error in onClick - " + ex.getMessage();
                    _txtStatus.setText(strErr);
                    Log.d(TAG, strErr);
                    _presenter.onSetEventLog(TAG + strErr, "Error", -1);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<PolicySetDataContract[]> loader) {
    }
}
