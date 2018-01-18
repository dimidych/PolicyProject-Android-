package com.dimidych.policydbworker;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class PolicySetFromServerAtLoader extends AsyncTaskLoader<Result<ArrayList<Map.Entry<PolicySetDataContract, String>>>> {
    private static final String LOG_TAG = "PolicySetServerATLoader";
    private DbWorker _dbWrkInst;
    private Context _context;

    public PolicySetFromServerAtLoader(Context context) {
        super(context);
        _context = context;

        if (_dbWrkInst == null)
            _dbWrkInst = new DbWorker(_context);
    }

    public PolicySetFromServerAtLoader(DbWorker dbWrkInst) {
        super(dbWrkInst.Context);
        _dbWrkInst=dbWrkInst;
        _context = _dbWrkInst.Context;
    }

    @Override
    public Result<ArrayList<Map.Entry<PolicySetDataContract, String>>> loadInBackground() {
        Result<ArrayList<Map.Entry<PolicySetDataContract, String>>> failedPolicies = new Result<>();

        try {
            String[] devinfo = Utils.getDeviceInfoAndPing(_context);

            if (devinfo == null || devinfo.length != 3)
                throw new Exception("Ошибка сети");

            CheckPolicyUtils checkPolicyUtils = new CheckPolicyUtils(_context);
            failedPolicies.SomeResult = new ArrayList<>();
            Result<PolicySetDataContract[]> policySetServiceResult = GetPolicySetAsyncTask.getPolicySet(
                    devinfo[0], devinfo[1], devinfo[2]);

            if (!policySetServiceResult.BoolRes || !TextUtils.isEmpty(policySetServiceResult.ErrorRes)
                    || policySetServiceResult.SomeResult == null || policySetServiceResult.SomeResult.length < 1)
                throw new Exception("Сервис не вернул результат. " + policySetServiceResult.ErrorRes);

            for (PolicySetDataContract servicePolicySet : policySetServiceResult.SomeResult) {
                PolicySetDataContract dbPolicySet = _dbWrkInst.getSinglePolicySetFromDb(servicePolicySet.PolicyId);

                if (dbPolicySet == null) {
                    if (!_dbWrkInst.addPolicySetToDb(servicePolicySet))
                        throw new Exception("Не удалось вставить набор политик с ид " + servicePolicySet.PolicySetId);
                } else {
                    if (!_dbWrkInst.updatePolicySet(servicePolicySet, dbPolicySet))
                        throw new Exception("Не удалось изменить набор политик с ид " + servicePolicySet.PolicySetId);
                }

                Result<String> checkPolicyResult = checkPolicyUtils.checkPolicy(servicePolicySet);
                servicePolicySet.Selected=checkPolicyResult.BoolRes;
                failedPolicies.SomeResult.add(new AbstractMap.SimpleEntry<>(servicePolicySet, checkPolicyResult.ErrorRes));
                _dbWrkInst.setEventLog(new EventLogDataContract(-1,
                    "Policy " + servicePolicySet.PolicyName + " was not set with error " + checkPolicyResult.ErrorRes, "", "Policy set error"));
            }

            failedPolicies.BoolRes=true;
        } catch (Exception ex) {
            _dbWrkInst.setEventLog(new EventLogDataContract(-1, ex.getMessage(), "", "Policy set loader error"));
            failedPolicies.ErrorRes = "Server Policy set loader error. " + ex.getMessage();
            Log.e(LOG_TAG, ex.getMessage());
        }

        return failedPolicies;
    }
}
