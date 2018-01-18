package com.dimidych.policyservicestarter;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dimidych.policydbworker.EventLogDataContract;

public class EventLogRecordFragment extends DialogFragment implements OnClickListener {

    private EventLogDataContract _eventLogRecord;

    public EventLogRecordFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getActivity().getIntent();
        _eventLogRecord = (EventLogDataContract) (intent.getParcelableExtra(EventLogDataContract.class.getCanonicalName()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("О проге...");
        View view = inflater.inflate(R.layout.fragment_event_log_record, null);
        view.findViewById(R.id.btnOk).setOnClickListener(this);
        ((TextView) (view.findViewById(R.id.lblDate))).setText("" + _eventLogRecord.EventLogDate);
        ((TextView) (view.findViewById(R.id.lblEvent))).setText(_eventLogRecord.EventName);
        ((TextView) (view.findViewById(R.id.lblDocument))).setText(_eventLogRecord.DocumentId == -1 ? "" : "Документ №" + _eventLogRecord.DocumentId);
        ((TextView) (view.findViewById(R.id.lblMessage))).setText(_eventLogRecord.Message);
        return view;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
