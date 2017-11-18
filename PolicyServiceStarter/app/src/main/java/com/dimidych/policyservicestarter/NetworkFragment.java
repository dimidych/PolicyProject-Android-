package com.dimidych.policyservicestarter;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dimidych.policydbworker.Result;
import com.dimidych.policydbworker.UserDataContract;
import com.dimidych.policyservicestarter.mvp.INetworkPresenterOps;
import com.dimidych.policyservicestarter.mvp.IPresenterOps;
import com.dimidych.policyservicestarter.mvp.IViewRequiredOps;
import com.dimidych.policyservicestarter.mvp.MvpFragment;
import com.dimidych.policyservicestarter.mvp.NetworkPresenter;

import java.util.Map;

public class NetworkFragment extends MvpFragment implements View.OnClickListener {
    private static final String TAG = "NetworkFragment";
    private final String ServiceName = "PolicyProjectManagementService/UserService/GetUserRest";
    private INetworkPresenterOps _presenter;
    private EditText _txtIpAddress;
    private EditText _txtPort;
    private EditText _txtTestResult;
    private Button _btnPing;
    private Button _btnTestService;
    private Button _btnSaveSettings;

    public NetworkFragment() {
        super(TAG);
    }

    @SuppressLint("ValidFragment")
    public NetworkFragment(FragmentManager fragmentManager) {
        super(fragmentManager, TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startMVPOps();
    }

    protected void initialize(IViewRequiredOps view)
            throws InstantiationException, IllegalAccessException {
        _presenter = new NetworkPresenter(view, this.getContext());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network, container, false);
        Map.Entry<String, String> connectionsettings = _presenter.getConnectionSettings();
        _txtIpAddress = (EditText) view.findViewById(R.id.txtIpAddress);
        _txtIpAddress.setText(connectionsettings.getKey());
        _txtPort = (EditText) view.findViewById(R.id.txtPort);
        _txtPort.setText(connectionsettings.getValue());
        _txtTestResult = (EditText) view.findViewById(R.id.txtTestResult);
        _btnPing = (Button) view.findViewById(R.id.btnPing);
        _btnPing.setOnClickListener(this);
        _btnTestService = (Button) view.findViewById(R.id.btnTestService);
        _btnTestService.setOnClickListener(this);
        _btnSaveSettings = (Button) view.findViewById(R.id.btnSaveSettings);
        _btnSaveSettings.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.btnPing:
                    PingTask pingTask = new PingTask();
                    pingTask.execute(_txtIpAddress.getText().toString());
                    break;

                case R.id.btnTestService:
                    TestServiceTask testSvcTask = new TestServiceTask();
                    testSvcTask.execute(_txtIpAddress.getText().toString(), _txtPort.getText().toString(), ServiceName);
                    break;

                case R.id.btnSaveSettings:
                    _presenter.updateConnectionSettings(_txtIpAddress.getText().toString(), _txtPort.getText().toString());
                    break;
            }
        } catch (Exception ex) {
            String strErr = "Error in onClick - " + ex.getMessage();
            Log.d(TAG, strErr);
        }
    }

    class TestServiceTask extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _txtTestResult.setText("Begin test service");
        }

        @Override
        protected Void doInBackground(String... params) {
            UserDataContract contract = new UserDataContract();
            contract.UserId = 1;
            Result<UserDataContract[]> result = _presenter.testUserService(params[0], params[1], params[2], contract);
            String resMsg = result.BoolRes ? ("Test success with test " + result.ErrorRes)
                    : ("Test failed with error " + result.ErrorRes);
            publishProgress(resMsg);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String oldText = _txtTestResult.getText().toString();
            _txtTestResult.setText(oldText + "\n" + values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            String oldText = _txtTestResult.getText().toString();
            _txtTestResult.setText(oldText + "\nFinish testing");
        }
    }

    class PingTask extends AsyncTask<String, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _txtTestResult.setText("Begin pinging");
        }

        @Override
        protected Void doInBackground(String... params) {
            for (int i = 0; i < 6; i++) {
                boolean result = _presenter.ping(params[0]);
                publishProgress(i, result ? 1 : 0);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            String oldText = _txtTestResult.getText().toString();
            _txtTestResult.setText(oldText + "\nPinging try #" + (values[0] + 1) + " was " + (values[1] == 1 ? "successfully" : "pizda"));
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            String oldText = _txtTestResult.getText().toString();
            _txtTestResult.setText(oldText + "\nFinish pinging");
        }
    }
}
