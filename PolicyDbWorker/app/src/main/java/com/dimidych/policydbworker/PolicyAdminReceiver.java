package com.dimidych.policydbworker;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PolicyAdminReceiver extends DeviceAdminReceiver {
    private static final String LOG_TAG = "PolicyAdminReceiver";

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(
                context,
                "Включены политики администрирования устройства",
                Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Включены политики администрирования устройства");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        super.onDisableRequested(context, intent);
        return "Получен запрос на отключение политик администрирования устройства";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(
                context,
                "Выключены политики администрирования устройства",
                Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Выключены политики администрирования устройства");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Toast.makeText(
                context,
                "Пароль изменен",
                Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, "Пароль изменен");
    }

    @Override
    public void onPasswordExpiring(Context context, Intent intent) {
        super.onPasswordExpiring(context, intent);

        Toast.makeText(
                context,
                "Период действия пароля подходит к концу. Измените пароль",
                Toast.LENGTH_LONG).show();

        DevicePolicyManager localDPM = (DevicePolicyManager) context
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName localComponent = new ComponentName(context,
                PolicyAdminReceiver.class);
        long expr = localDPM.getPasswordExpiration(localComponent);
        long delta = expr - System.currentTimeMillis();
        boolean expired = delta < 0L;

        if (expired) {
            localDPM.setPasswordExpirationTimeout(localComponent, 10000L);
            Intent passwordChangeIntent = new Intent(
                    DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
            passwordChangeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(passwordChangeIntent);
        }
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent){
        super.onPasswordFailed(context,intent);
    }
}
