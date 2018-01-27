package com.dimidych.policydbworker;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class PolicySetAsyncTaskLoader extends AsyncTaskLoader<Result<ArrayList<Map.Entry<PolicySetDataContract, String>>>> {
    private static final String LOG_TAG = "PolicySetATLoader";
    private DbWorker _dbWrkInst;
    private Context _context;

    public PolicySetAsyncTaskLoader(Context context) {
        super(context);
        _context = context;

        if (_dbWrkInst == null)
            _dbWrkInst = new DbWorker(_context);
    }

    public PolicySetAsyncTaskLoader(DbWorker dbWrkInst) {
        super(dbWrkInst.Context);
        _dbWrkInst = dbWrkInst;
        _context = _dbWrkInst.Context;
    }

    @Override
    public Result<ArrayList<Map.Entry<PolicySetDataContract, String>>> loadInBackground() {
        Result<ArrayList<Map.Entry<PolicySetDataContract, String>>> failedPolicies = new Result<>();

        try {
            PolicySetFromServerAtLoader serverPolicySetLoader = new PolicySetFromServerAtLoader(_dbWrkInst);
            Result<ArrayList<Map.Entry<PolicySetDataContract, String>>> serverPolicySetsRes = serverPolicySetLoader.loadInBackground();

            if (serverPolicySetsRes.BoolRes)
                return serverPolicySetsRes;

            CheckPolicyUtils checkPolicyUtils = new CheckPolicyUtils(_context);
            failedPolicies.SomeResult = new ArrayList<>();
            PolicySetDataContract[] policySetFromDb = _dbWrkInst.getPolicySetFromDb();

            if (policySetFromDb == null)
                return null;

            for (PolicySetDataContract policySet : policySetFromDb) {
                Result<String> checkPolicyResult = checkPolicyUtils.checkPolicy(policySet);
                policySet.Selected = checkPolicyResult.BoolRes;
                failedPolicies.SomeResult.add(new AbstractMap.SimpleEntry<>(policySet, checkPolicyResult.ErrorRes));
                _dbWrkInst.setEventLog(new EventLogDataContract(-1,
                        "Policy " + policySet.PolicyName + " was not set with error " + checkPolicyResult.ErrorRes, "", "Policy set error"));
            }

            failedPolicies.BoolRes = true;
        } catch (Exception ex) {
            _dbWrkInst.setEventLog(new EventLogDataContract(-1, ex.getMessage(), "", "Policy set loader error"));
            failedPolicies.ErrorRes = "Policy set loader error. " + ex.getMessage();
            Log.e(LOG_TAG, ex.getMessage());
        }

        return failedPolicies;
    }
}
