package com.dimidych.policyservicestarter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dimidych.policyservicestarter.mvp.IPresenterOps;
import com.dimidych.policyservicestarter.mvp.IServicePresenterOps;
import com.dimidych.policyservicestarter.mvp.IViewRequiredOps;
import com.dimidych.policyservicestarter.mvp.MvpFragment;
import com.dimidych.policyservicestarter.mvp.ServicePresenter;

public class ServiceFragment extends MvpFragment
        implements View.OnClickListener  {
    public static final String TAG="ServiceFragment";
    private IServicePresenterOps _presenter;
    private EditText _txtLog;
    private Button _btnStartService;
    private Button _btnStopService;

    public ServiceFragment() {
        super(TAG);
    }

    @SuppressLint("ValidFragment")
    public ServiceFragment(FragmentManager fragmentManager) {
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
        View view=inflater.inflate(R.layout.fragment_service, container, false);
        _txtLog=(EditText) view.findViewById(R.id.txtServiceLog);
        _btnStartService=(Button)view.findViewById(R.id.btnStartService);
        _btnStartService.setOnClickListener(this);
        _btnStopService=(Button)view.findViewById(R.id.btnStopService);
        _btnStopService.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.btnStartService:
                    _txtLog.setText(_presenter.startService());
                    _presenter.onSetEventLog(TAG + "Служба запущена", "Уведомление", -1);
                    break;

                case R.id.btnStopService:
                    _txtLog.setText(_presenter.stopService());
                    _presenter.onSetEventLog(TAG + "Служба остановлена", "Уведомление", -1);
                    break;
            }
        } catch (Exception ex) {
            String strErr = " Error in onClick - " + ex.getMessage();
            Log.d(TAG, strErr);
            _presenter.onSetEventLog(TAG + strErr, "Error", -1);
        }
    }

    @Override
    protected void initialize(IViewRequiredOps view)
            throws InstantiationException, IllegalAccessException {
        _presenter = new ServicePresenter(view, getContext());
        _stateMaintainer.put(IPresenterOps.class.getSimpleName(), _presenter);
    }

    @Override
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
}
