package com.dimidych.policyservicestarter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dimidych.policyservicestarter.mvp.GetCertPresenter;
import com.dimidych.policyservicestarter.mvp.IGetCertPresenterOps;
import com.dimidych.policyservicestarter.mvp.IPresenterOps;
import com.dimidych.policyservicestarter.mvp.IViewRequiredOps;
import com.dimidych.policyservicestarter.mvp.MvpFragment;

public class GetCertFragment extends MvpFragment
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<String> {
    private static final String TAG = "GetCertFragment";
    private IGetCertPresenterOps _presenter;
    private EditText _txtCertificate;
    private Button _btnGetCert;
    public static final int LOADER_ID = 1;
    private Loader<String> _loader;

    public GetCertFragment() {
        super(TAG);
    }

    @SuppressLint("ValidFragment")
    public GetCertFragment(FragmentManager fragmentManager) {
        super(fragmentManager, TAG);
    }

    protected void initialize(IViewRequiredOps view)
            throws InstantiationException, IllegalAccessException {
        _presenter = new GetCertPresenter(view, this.getContext());
        _stateMaintainer.put(IPresenterOps.class.getSimpleName(), _presenter);
    }

    protected void reinitialize(IViewRequiredOps view)
            throws InstantiationException, IllegalAccessException {
        _presenter = _stateMaintainer.get(IPresenterOps.class.getSimpleName());

        if (_presenter == null) {
            Log.w(TAG, "recreating Presenter");
            initialize(view);
        } else {
            _presenter.onConfigurationChanged(view);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startMVPOps();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_cert, container, false);
        _txtCertificate = (EditText) view.findViewById(R.id.txtCertificate);
        _loader = getLoaderManager().initLoader(LOADER_ID, null, this);
        _btnGetCert = (Button) view.findViewById(R.id.btnGetCert);
        _btnGetCert.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        _loader.onContentChanged();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        try {
            if (id == LOADER_ID)
                reinitialize(this);
        } catch (Exception ex) {
            String strErr = " Error in onCreateLoader - " + ex.getMessage();
            _txtCertificate.setText(strErr);
            Log.d(TAG, strErr);
            _presenter.onSetEventLog(TAG+strErr,"Error",-1);
        }

        return (AsyncTaskLoader<String>)_presenter;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        switch (loader.getId()) {
            case LOADER_ID:
                _txtCertificate.setText(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }
}
