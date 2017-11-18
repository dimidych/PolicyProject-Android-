package com.dimidych.policydbworker;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetLoginDevicesAsyncTask extends AsyncTask<String, Void, Result<LoginDevicesDataContract[]>> {
    private static final String LOG_TAG = "GetLoginDevicesAT";

    @Override
    protected Result<LoginDevicesDataContract[]> doInBackground(String... params) {
        String serverIpAddress = params[0];
        String serverPort = params[1];
        String deviceSerial = params[2];
        return getLoginDevicesInfo(serverIpAddress, serverPort, deviceSerial);
    }

    private static Result<DeviceInfoDataContract[]> getDeviceInfoFromService(String serverIpAddress,
                                                                             String serverPort, String deviceSerial) throws Exception {
        DeviceInfoDataContract deviceInfo = null;

        if (deviceSerial.trim() != "") {
            deviceInfo = new DeviceInfoDataContract();
            deviceInfo.DeviceSerialNumber = deviceSerial;
        }

        String serviceResult = NetworkWorker.restServiceCall(serverIpAddress, serverPort,
                "/PolicyProjectManagementService/DeviceService/GetDeviceRest", deviceInfo);

        if (TextUtils.isEmpty(serviceResult))
            throw new Exception("Ошибка выполнения запроса к службе");

        Gson gson = new Gson();
        Result<DeviceInfoDataContract[]> result = gson.fromJson(serviceResult, new TypeToken<Result<DeviceInfoDataContract[]>>() {
        }.getType());

        if (!result.BoolRes)
            Log.e(LOG_TAG, "Ошибка вызова веб-службы. " + result.ErrorRes);

        result.ErrorRes = serviceResult;
        return result;
    }

    public static Result<LoginDevicesDataContract[]> getLoginDevicesInfo(String serverIpAddress, String serverPort, String deviceSerial) {
        Result<LoginDevicesDataContract[]> result = new Result<>();

        try {
            Result<DeviceInfoDataContract[]> devInfos = getDeviceInfoFromService(serverIpAddress, serverPort, deviceSerial);

            if (!devInfos.BoolRes || devInfos.SomeResult == null || devInfos.SomeResult.length < 1)
                throw new Exception("Ошибка получения информации об устройстве. " + devInfos.ErrorRes);

            LoginDevicesDataContract loginDevice = new LoginDevicesDataContract();
            loginDevice.DeviceId = devInfos.SomeResult[0].DeviceId;
            String loginDeviceInfo = NetworkWorker.restServiceCall(serverIpAddress, serverPort,
                    "/PolicyProjectManagementService/LoginDevicesService/GetLoginDevicesRest", loginDevice);

            if (TextUtils.isEmpty(loginDeviceInfo))
                throw new Exception("Ошибка выполнения запроса к службе");

            Gson gson = new Gson();
            result = gson.fromJson(loginDeviceInfo, new TypeToken<Result<LoginDevicesDataContract[]>>() {
            }.getType());

            if (!result.BoolRes)
                Log.e(LOG_TAG, "Ошибка вызова веб-службы. " + result.ErrorRes);

            result.ErrorRes = loginDeviceInfo;
            return result;
        } catch (Exception ex) {
            result.ErrorRes = ex.getMessage();
            Log.e(LOG_TAG, result.ErrorRes);
        }

        return result;
    }
}
