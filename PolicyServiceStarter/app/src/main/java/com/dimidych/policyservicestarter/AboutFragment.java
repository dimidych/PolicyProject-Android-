package com.dimidych.policyservicestarter;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dimidych.policydbworker.DevicePolicyAdmin;

public class AboutFragment extends Fragment implements View.OnClickListener {
    public AboutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        Button btnCancelAdmin = (Button) view.findViewById(R.id.btnDisableAdmin);
        btnCancelAdmin.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        Activity activity = this.getActivity();
        DevicePolicyAdmin.removeActiveAdmin(activity.getApplicationContext());
        Snackbar.make(this.getView(), "Политики администрирования удалены", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
