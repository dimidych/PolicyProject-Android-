package com.dimidych.policyservicestarter;

import android.app.FragmentManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dimidych.policydbworker.EventLogDataContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogViewerAdapter extends BaseAdapter {
    private LayoutInflater _inflater;
    private EventLogDataContract[] _eventArray;
    private final String LOG_TAG = "LogViewerAdapter";
    private final Context _context;
    private final FragmentManager _fragmentManager;

    public LogViewerAdapter(Context context, EventLogDataContract[] eventArray, FragmentManager fragmentManager) {
        _eventArray = eventArray;
        _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _context = context;
        _fragmentManager = fragmentManager;
    }

    @Override
    public int getCount() {
        int result;

        try {
            if (_eventArray == null)
                throw new Exception("Пустой набор данных");

            result = _eventArray.length;
        } catch (Exception ex) {
            String strErr = "Ошибка получения количества - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return 0;
        }

        return result;
    }

    @Override
    public Object getItem(int position) {
        Object result;

        try {
            if (_eventArray == null)
                throw new Exception("Пустой набор данных");

            result = _eventArray[position];
        } catch (Exception ex) {
            String strErr = "Ошибка получения элемента - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return null;
        }

        return result;
    }

    @Override
    public long getItemId(int position) {
        long result;

        try {
            if (_eventArray == null)
                throw new Exception("Пустой набор данных");

            if (_eventArray.length == 0 && position >= _eventArray.length)
                throw new Exception("Пустой набор данных");

            result = _eventArray[position].EventId;
        } catch (Exception ex) {
            String strErr = "Ошибка получения ид элемента - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return 0;
        }

        return result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View resultView = convertView;

        try {
            if (resultView == null)
                resultView = _inflater.inflate(R.layout.layout_event_item, parent, false);

            if ((_eventArray == null || _eventArray.length == 0 || position >= _eventArray.length))
                throw new Exception("Несоответствие набора записей");

            final EventLogDataContract eventLog = _eventArray[position];
            Button btnEventLog = (Button) resultView.findViewById(R.id.btnDate);
            btnEventLog.setText(ConvertDateToString(eventLog.EventLogDate));
            btnEventLog.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                EventLogRecordFragment eventRecFragment = new EventLogRecordFragment();
                                eventRecFragment.EventLogRecord = eventLog;
                                eventRecFragment.show(_fragmentManager, "Cобытие");
                            } catch (Exception ex) {
                                Log.d(LOG_TAG, ex.getMessage());
                            }
                        }
                    }
            );
            TextView txtEventName = (TextView) resultView.findViewById(R.id.txtEventName);
            txtEventName.setText(eventLog.EventName);
        } catch (Exception ex) {
            String strErr = "Ошибка cоздания списка  - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return null;
        }

        return resultView;
    }

    public static String ConvertDateToString(Date date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }
}
