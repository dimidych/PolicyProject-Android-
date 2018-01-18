package com.dimidych.policydbworker;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.AbstractMap;
import java.util.Map;

public class GetLoginInfoAsyncTaskLoader extends AsyncTaskLoader<Result<Map.Entry<String,String>>> {
    private static final String LOG_TAG = "GetLoginInfoAT";
    private DbWorker _dbWrkInst;
    private Context _context;

    public GetLoginInfoAsyncTaskLoader(Context context) {
        super(context);
        _context = context;

        if (_dbWrkInst == null)
            _dbWrkInst = new DbWorker(_context);
    }

    public GetLoginInfoAsyncTaskLoader(DbWorker dbWrkInst) {
        super(dbWrkInst.Context);
        _dbWrkInst = dbWrkInst;
        _context = _dbWrkInst.Context;
    }

    @Override
    public Result<Map.Entry<String, String>> loadInBackground() {
        Result<Map.Entry<String, String>> result = new Result<>();

        try {
            String[] devinfo = Utils.getDeviceInfoAndPing(_context);

            if (devinfo == null || devinfo.length != 3)
                throw new Exception("Не удалось получить информацию об устройстве");

            Result<LoginDevicesDataContract[]> loginDevices = GetLoginDevicesAsyncTask.getLoginDevicesInfo(
                    devinfo[0], devinfo[1], devinfo[2]);

            if (!loginDevices.BoolRes || loginDevices.SomeResult == null || loginDevices.SomeResult.length < 1)
                throw new Exception("Ошибка получения информации об авторизации устройства. " + loginDevices.ErrorRes);

            String serviceResult = NetworkWorker.restServiceCall(devinfo[0], devinfo[1],
                    "/PolicyProjectManagementService/LoginService/GetCertificateRest", loginDevices.SomeResult[0].LoginId);

            if (TextUtils.isEmpty(serviceResult))
                throw new Exception("Ошибка выполнения запроса к службе");

            Gson gson = new Gson();
            Result<String[]> loginInfo = gson.fromJson(serviceResult, new TypeToken<Result<String[]>>() {
            }.getType());

            if (!loginInfo.BoolRes || loginInfo.SomeResult == null || loginInfo.SomeResult.length < 1)
                Log.e(LOG_TAG, "Ошибка получения информации о логине. " + result.ErrorRes);

            result.SomeResult = new AbstractMap.SimpleEntry<>(loginInfo.SomeResult[0], loginInfo.SomeResult[1]);
            result.BoolRes = true;
        } catch (Exception ex) {
            result.ErrorRes = " error in GetLoginInfoAsyncTaskLoader - " + ex.getMessage();
            Log.e(LOG_TAG, result.ErrorRes);
            _dbWrkInst.onSetLog(result.ErrorRes, "Error", -1);
        }

        return result;
    }
}
