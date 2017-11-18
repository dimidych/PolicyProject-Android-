package com.dimidych.policydbworker;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetPolicySetAsyncTask extends AsyncTask<String, Void, Result<PolicySetDataContract[]>> {
    private static final String LOG_TAG = "GetPolicySetAsyncTask";

    @Override
    protected Result<PolicySetDataContract[]> doInBackground(String... params) {
        String serverIpAddress = params[0];
        String serverPort = params[1];
        String deviceSerial = params[2];
        return getPolicySet(serverIpAddress, serverPort, deviceSerial);
    }

    public static Result<PolicySetDataContract[]> getPolicySet(String serverIpAddress, String serverPort, String deviceSerial) {
        Result<PolicySetDataContract[]> result = new Result<>();

        try {
            Result<LoginDevicesDataContract[]> loginDevices = GetLoginDevicesAsyncTask.getLoginDevicesInfo(serverIpAddress, serverPort, deviceSerial);

            if (!loginDevices.BoolRes || loginDevices.SomeResult == null || loginDevices.SomeResult.length < 1)
                throw new Exception("Ошибка получения информации об авторизации устройства. " + loginDevices.ErrorRes);

            PolicySetDataContract policySetContract = new PolicySetDataContract();
            policySetContract.LoginId = loginDevices.SomeResult[0].LoginId;

            String serviceResult = NetworkWorker.restServiceCall(serverIpAddress, serverPort,
                    "/PolicyProjectManagementService/PolicySetService/GetPolicySetForLoginRest", policySetContract);

            if (TextUtils.isEmpty(serviceResult))
                throw new Exception("Ошибка выполнения запроса к службе");

            Gson gson = new Gson();
            result = gson.fromJson(serviceResult, new TypeToken<Result<PolicySetDataContract[]>>() {
            }.getType());

            if (!result.BoolRes || result.SomeResult == null || result.SomeResult.length < 1)
                Log.e(LOG_TAG, "Ошибка получения набора политик. " + result.ErrorRes);
        } catch (Exception ex) {
            result.ErrorRes = ex.getMessage();
            Log.e(LOG_TAG, result.ErrorRes);
        }

        return result;
    }
}