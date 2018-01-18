package com.dimidych.policydbworker;

import android.content.Context;

import java.util.Map;

public class Utils {
    private static final String LOG_TAG = "Utils";

    public static String[] getDeviceInfoAndPing(Context context) throws Exception {
        DbWorker dbWrkInst = new DbWorker(context);
        Map.Entry<String, String> connectionSettings = dbWrkInst.getConnectionSettings();
        String serverIpAddress = connectionSettings.getKey();
        String serverPort = connectionSettings.getValue();
        int counter = 3;

        for (int i = 0; i < 3; i++)
            if (NetworkWorker.ping(serverIpAddress))
                counter--;

        if (counter != 0)
            throw new Exception("Не удалось установить соединение с удаленым сервером");

        DeviceInfo devInfo = DeviceInfo.getDeviceInfo();
        return new String[]{serverIpAddress, serverPort, devInfo == null || devInfo.DeviceSerial == null ? "4cc156590604" : devInfo.DeviceSerial};
    }
}
