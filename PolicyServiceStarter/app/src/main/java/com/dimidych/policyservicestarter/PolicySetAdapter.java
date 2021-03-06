package com.dimidych.policyservicestarter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.dimidych.policydbworker.PolicySetDataContract;

class PolicySetAdapter extends BaseAdapter {
    private LayoutInflater _inflater;
    private PolicySetDataContract[] _policySetArr;
    private final String LOG_TAG = "PolicySetAdapter";

    PolicySetAdapter(Context context, PolicySetDataContract[] policySetArr) {
        _policySetArr = policySetArr;
        _inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        int result;

        try {
            if (_policySetArr == null)
                throw new Exception("Пустой набор данных");

            result = _policySetArr.length;
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
            if (_policySetArr == null)
                throw new Exception("Пустой набор данных");

            result = _policySetArr[position];
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
            if (_policySetArr == null)
                throw new Exception("Пустой набор данных");

            if (_policySetArr.length == 0 && position >= _policySetArr.length)
                throw new Exception("Пустой набор данных");

            result = _policySetArr[position].PolicySetId;
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
                resultView = _inflater.inflate(R.layout.layout_policy_item, parent, false);

            if ((_policySetArr == null || _policySetArr.length == 0 || position >= _policySetArr.length))
                throw new Exception("Несоответствие набора записей");

            PolicySetDataContract policySet = _policySetArr[position];
            CheckBox chkPolicy = (CheckBox) resultView.findViewById(R.id.chkPolicy);
            chkPolicy.setText(policySet.PolicyName);
            chkPolicy.setChecked(policySet.Selected);
            int[] colors = new int[]{Color.parseColor("#ffffffff"), Color.parseColor("#ffd6d7d7"), Color.parseColor("#ffffbb33")};
            resultView.setBackgroundColor(colors[position % 2]);

            if (!policySet.Selected)
                resultView.setBackgroundColor(colors[2]);
        } catch (Exception ex) {
            String strErr = "Ошибка cоздания списка  - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return null;
        }

        return resultView;
    }
}
