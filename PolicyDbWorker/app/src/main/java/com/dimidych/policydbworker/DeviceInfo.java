package com.dimidych.policydbworker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class DeviceInfo {
    private static final String LOG_TAG = "DeviceInfo";
    public String DeviceName;
    public String DeviceSerial;
    public String DeviceId;
    public String IpAddress;
    public String MacAddress;
    public boolean IsWiFi;

    public DeviceInfo() {
    }

    @SuppressWarnings("deprecation")
    public static DeviceInfo getDeviceInfo(Context ctx) {
        DeviceInfo devInfo = new DeviceInfo();

        try {
            devInfo.DeviceId = Build.ID;
            devInfo.DeviceName = Build.MODEL;
            devInfo.DeviceSerial = Build.SERIAL;
            ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            devInfo.IsWiFi = networkInfo.getTypeName().equalsIgnoreCase("wifi");

            if (devInfo.IsWiFi) {
                WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                devInfo.MacAddress = wInfo.getMacAddress();
                devInfo.IpAddress = Formatter.formatIpAddress(wInfo.getIpAddress());
            } else {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                         en.hasMoreElements(); ) {
                        NetworkInterface networkinterface = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = networkinterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress()) {
                                devInfo.IpAddress = inetAddress.getHostAddress().toString();
                                devInfo.MacAddress = "";
                                break;
                            }
                        }
                    }
                } catch (java.net.SocketException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Ошибка получения информации об устройстве  - " + ex.getMessage());
        }

        return devInfo;
    }

    public static DeviceInfo getDeviceInfo(){
        DeviceInfo devInfo = new DeviceInfo();
        devInfo.DeviceId = "NRD90M";
        devInfo.DeviceName = "Xiaomi Redmi Note4";
        devInfo.DeviceSerial = "4cc156590604";
        devInfo.IsWiFi =true;
        devInfo.MacAddress ="02:00:00:00:00:00";
        devInfo.IpAddress ="192.168.1.102";
        return devInfo;
    }
}
