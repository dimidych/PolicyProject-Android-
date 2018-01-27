package com.dimidych.policyservicestarter;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dimidych.policydbworker.EventLogDataContract;

public class EventLogRecordFragment extends DialogFragment implements OnClickListener {

    public EventLogDataContract EventLogRecord;

    public EventLogRecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("О событии...");
        View view = inflater.inflate(R.layout.fragment_event_log_record, null);

        try {
            view.findViewById(R.id.btnOk).setOnClickListener(this);
            ((TextView) (view.findViewById(R.id.lblDate))).setText(LogViewerAdapter.ConvertDateToString(EventLogRecord.EventLogDate));
            ((TextView) (view.findViewById(R.id.lblEvent))).setText(EventLogRecord.EventName);
            ((TextView) (view.findViewById(R.id.lblDocument))).setText(EventLogRecord.DocumentId == -1 ? "" : "Документ №" + EventLogRecord.DocumentId);
            ((TextView) (view.findViewById(R.id.lblMessage))).setText(EventLogRecord.Message);
        } catch (Exception ex) {
            Log.e("EventLogRecordFragment", "Error while show event record fragment");
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
