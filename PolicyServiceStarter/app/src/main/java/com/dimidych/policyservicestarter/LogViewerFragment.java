package com.dimidych.policyservicestarter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.dimidych.policydbworker.EventLogDataContract;
import com.dimidych.policyservicestarter.mvp.ILogViewerPresenterOps;
import com.dimidych.policyservicestarter.mvp.IPresenterOps;
import com.dimidych.policyservicestarter.mvp.IViewRequiredOps;
import com.dimidych.policyservicestarter.mvp.LogViewerPresenter;
import com.dimidych.policyservicestarter.mvp.MvpFragment;

import java.util.Calendar;

public class LogViewerFragment extends MvpFragment
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<EventLogDataContract[]> {
    private static final String TAG = "LogViewerFragment";
    private ILogViewerPresenterOps _presenter;
    public static final int LOADER_ID = 252;
    private Loader<EventLogDataContract[]> _loader;
    private Button _btnFrom;
    private Button _btnTo;
    private Button _btnFind;
    private EditText _txtEvent;
    private ListView _lstEvent;
    private Calendar _clndrFrom = Calendar.getInstance();
    private Calendar _clndrTo = Calendar.getInstance();

    public LogViewerFragment() {
        super(TAG);
    }

    @SuppressLint("ValidFragment")
    public LogViewerFragment(FragmentManager fragmentManager) {
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
        View view = inflater.inflate(R.layout.fragment_log_viewer, container, false);
        _btnFrom = (Button) view.findViewById(R.id.btnFrom);
        _btnFrom.setText(getStringDatePresent(_clndrFrom));
        _btnTo = (Button) view.findViewById(R.id.btnTo);
        _btnTo.setText(getStringDatePresent(_clndrTo));
        _btnFind = (Button) view.findViewById(R.id.btnFind);
        _txtEvent = (EditText) view.findViewById(R.id.txtEvent);
        _lstEvent = (ListView) view.findViewById(R.id.lstEvents);
        _btnFrom.setOnClickListener(this);
        _btnTo.setOnClickListener(this);
        _btnFind.setOnClickListener(this);
        _loader = getLoaderManager().initLoader(LOADER_ID, null, this);
        return view;
    }

    private void reinitLoader() {
        LogViewerPresenter presenter = (LogViewerPresenter) _presenter;
        presenter.FromDate = _btnFrom.getText().toString();
        presenter.ToDate = _btnTo.getText().toString();
        presenter.EventName = _txtEvent.getText().toString();
        _loader.onContentChanged();
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.btnFrom: {
                    DateTimePickerFragment dateFromFragment = new DateTimePickerFragment();
                    dateFromFragment.setTargetFragment(this, Constants.DLG_DATE_FROM);
                    dateFromFragment.show(getActivity().getSupportFragmentManager(), "dateFromFragment");
                }
                break;

                case R.id.btnTo: {
                    DateTimePickerFragment dateToFragment = new DateTimePickerFragment();
                    dateToFragment.setTargetFragment(this, Constants.DLG_DATE_TO);
                    dateToFragment.show(getActivity().getSupportFragmentManager(), "dateToFragment");
                }
                break;

                case R.id.btnFind:
                    reinitLoader();
                    break;
            }
        } catch (Exception ex) {
            String strErr = " Error in onClick - " + ex.getMessage();
            Log.d(TAG, strErr);
            _presenter.onSetEventLog(TAG + strErr, "Error", -1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Constants.DIALOG_CANCEL)
                return;

            if (data == null)
                return;

            if (requestCode == Constants.DLG_DATE_FROM || requestCode == Constants.DLG_DATE_TO) {
                int year = data.getIntExtra("selected_year", 2010);
                int month = data.getIntExtra("selected_month", 1);
                int day = data.getIntExtra("selected_day", 1);

                if (requestCode == Constants.DLG_DATE_FROM) {
                    _clndrFrom.set(year, month, day);
                    _btnFrom.setText(getStringDatePresent(_clndrFrom));
                }

                if (requestCode == Constants.DLG_DATE_TO) {
                    _clndrTo.set(year, month, day);
                    _clndrTo.add(Calendar.DATE, 1);
                    _btnTo.setText(getStringDatePresent(_clndrTo));
                }
            }
        } catch (Exception ex) {
            String strErr = " Error while getting child activity data - " + ex.getMessage();
            Log.d(TAG, strErr);
            _presenter.onSetEventLog(TAG + strErr, "Error", -1);
        }
    }

    @Override
    protected void initialize(IViewRequiredOps view)
            throws InstantiationException, IllegalAccessException {
        _presenter = new LogViewerPresenter(view, this.getContext());
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

    public static String getStringDatePresent(Calendar clndr) {
        String month = (clndr.get(Calendar.MONTH) + 1) < 10 ? ("0" + (clndr.get(Calendar.MONTH) + 1)) : ((clndr.get(Calendar.MONTH) + 1) + "");
        String day = clndr.get(Calendar.DAY_OF_MONTH) < 10 ? ("0" + clndr.get(Calendar.DAY_OF_MONTH)) : (clndr.get(Calendar.DAY_OF_MONTH) + "");
        return "" + clndr.get(Calendar.YEAR) + "-" + month + "-" + day;
    }

    @Override
    public Loader<EventLogDataContract[]> onCreateLoader(int id, Bundle args) {
        try {
            if (id == LOADER_ID)
                reinitialize(this);
        } catch (Exception ex) {
            String strErr = " Error in onCreateLoader - " + ex.getMessage();
            Log.d(TAG, strErr);
            _presenter.onSetEventLog(TAG + strErr, "Error", -1);
        }

        return (Loader<EventLogDataContract[]>) _presenter;
    }

    @Override
    public void onLoadFinished(Loader<EventLogDataContract[]> loader, EventLogDataContract[] data) {
        switch (loader.getId()) {
            case LOADER_ID:
                try {
                    if (data == null || data.length < 1) {
                        showToast("Нет логов");
                        return;
                    }

                    LogViewerAdapter logViewerAdapter = new LogViewerAdapter(getContext(), data, getActivity().getFragmentManager());
                    _lstEvent.setAdapter(logViewerAdapter);
                    logViewerAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    String strErr = " Error in onLoadFinished - " + ex.getMessage();
                    showAlert(strErr);
                    Log.d(TAG, strErr);
                    _presenter.onSetEventLog(TAG + strErr, "Error", -1);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<EventLogDataContract[]> loader) {
    }
}
