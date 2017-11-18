package com.dimidych.policycheckertest;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dimidych.policydbworker.CheckPolicyUtils;
import com.dimidych.policydbworker.DevicePolicyAdmin;
import com.dimidych.policydbworker.PolicyAdminReceiver;
import com.dimidych.policydbworker.PolicySetDataContract;
import com.dimidych.policydbworker.Result;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "CheckPolicy";
    private TextView _txtStatus;
    private ListView _lstCheckedPolicies;
    private Button _btnCheckPolicies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName policyDeviceAdmin = new ComponentName(getApplicationContext(),
                PolicyAdminReceiver.class);
        DevicePolicyAdmin.requestAdminService(this, policyDeviceAdmin);
        _txtStatus = (TextView) findViewById(R.id.txtStatus);
        _lstCheckedPolicies = (ListView) findViewById(R.id.lstCheckedPolicies);
        _btnCheckPolicies = (Button) findViewById(R.id.btnCheckPolicies);
        _btnCheckPolicies.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            PolicySetDataContract[] policyArr = new PolicySetDataContract[7];
            CheckPolicyUtils chkPolicyUtil=new CheckPolicyUtils(this);

            for(PolicySetDataContract policySet:policyArr){
                Result<String> checkResult=chkPolicyUtil.checkPolicy(policySet);
                policySet.Selected=checkResult.BoolRes;
            }

            PolicySetAdapter policySetAdapter=new PolicySetAdapter(this,policyArr);
            _lstCheckedPolicies.setAdapter(policySetAdapter);
        }
        catch(Exception ex){
            String error="Error while check - "+ex.getMessage();
            _txtStatus.setText(error);
        }
    }
}
