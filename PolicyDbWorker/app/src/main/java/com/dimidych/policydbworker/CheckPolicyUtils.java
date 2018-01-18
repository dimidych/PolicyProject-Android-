package com.dimidych.policydbworker;

import android.app.admin.DevicePolicyManager;
import android.app.admin.SecurityLog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CheckPolicyUtils {
    private static final String LOG_TAG = "CheckPolicyUtils";
    private Context _context;

    public CheckPolicyUtils(Context context) {
        _context = context;
    }

    public Result<String> checkPolicy(PolicySetDataContract policySet) {
        Result<String> result = new Result<>();
        result.SomeResult = policySet.PolicyInstruction;

        try {
            switch (policySet.PolicyInstruction) {
                case "USES_POLICY_DISABLE_CAMERA": {
                    if (TextUtils.isEmpty(policySet.PolicyParam))
                        throw new Exception("Параметры не заданы");

                    if (!DevicePolicyAdmin.turnOnOffCamera(_context, Boolean.parseBoolean(policySet.PolicyParam)))
                        throw new Exception("Не удалось включить/отключить камеру");
                    break;
                }

                case "USES_POLICY_EXPIRE_PASSWORD": {
                    long currentValue = DevicePolicyAdmin.getPasswordExpirationPeriod(_context);
                    long delta = currentValue - System.currentTimeMillis();
                    boolean expired = delta < 0L;

                    if (expired)
                        throw new Exception("Период действия пароля истек");
                    break;
                }

                //Extra action
                case "WIPE_EXTERNAL_STORAGE": {
                    if (TextUtils.isEmpty(policySet.PolicyParam))
                        throw new Exception("Параметры не заданы");

                    boolean wipeExtStorage = Boolean.parseBoolean(policySet.PolicyParam.split(";")[0]);
                    boolean wipeFactoryReset = Boolean.parseBoolean(policySet.PolicyParam.split(";")[1]);

                    if (!DevicePolicyAdmin.wipeData(_context, wipeExtStorage, wipeFactoryReset))
                        throw new Exception("Не удалось стереть внешнее хранилище");
                    break;
                }

                case "USES_ENCRYPTED_STORAGE": {
                    if (TextUtils.isEmpty(policySet.PolicyParam))
                        throw new Exception("Параметры не заданы");

                    boolean expected = Boolean.parseBoolean(policySet.PolicyParam);
                    boolean current = DevicePolicyAdmin.getStorageEncryption(_context);

                    if (current != expected)
                        if (!DevicePolicyAdmin.setStorageEncryption(_context, expected))
                            throw new Exception("Не удалось применить политику шифрования диска");
                    break;
                }

                //Extra action
                case "USES_POLICY_FORCE_LOCK": {
                    if (!DevicePolicyAdmin.lockNow(_context))
                        throw new Exception("Не удалось заблокировать устройство");
                    break;
                }

                case "USES_POLICY_LIMIT_PASSWORD": {
                    if (TextUtils.isEmpty(policySet.PolicyParam))
                        throw new Exception("Параметры не заданы");

                    String[] expectedParams = policySet.PolicyParam.split(";");

                    if (expectedParams.length != 5)
                        throw new Exception("Количество параметров не совпадает");

                    int minLength = Integer.parseInt(expectedParams[0]);
                    int minUpperCaseLetters = Integer.parseInt(expectedParams[1]);
                    boolean isAlphaNumeric = Boolean.parseBoolean(expectedParams[2]);
                    int minNumeric = Integer.parseInt(expectedParams[3]);
                    int minLetter = Integer.parseInt(expectedParams[4]);

                    if(!DevicePolicyAdmin.checkPasswordQuality(_context, minLength, minUpperCaseLetters, isAlphaNumeric, minNumeric, minLetter)) {
                        if (!DevicePolicyAdmin.setPasswordQuality(_context, minLength, minUpperCaseLetters, isAlphaNumeric, minNumeric, minLetter))
                            throw new Exception("Не удалось применить политику требований к паролю");
                    }
                    break;
                }

                //Extra action
                case "USES_POLICY_RESET_PASSWORD": {
                    if (TextUtils.isEmpty(policySet.PolicyParam))
                        throw new Exception("Параметры не заданы");

                    if (!DevicePolicyAdmin.resetPassword(_context, policySet.PolicyParam, DevicePolicyManager.RESET_PASSWORD_DO_NOT_ASK_CREDENTIALS_ON_BOOT))
                        throw new Exception("Не удалось сбросить пароль");
                    break;
                }

                case "USES_POLICY_MAXIMUM_FAILED_PWD_WIPE": {
                    if (TextUtils.isEmpty(policySet.PolicyParam))
                        throw new Exception("Параметры не заданы");

                    int expected = Integer.parseInt(policySet.PolicyParam);
                    int current = DevicePolicyAdmin.getMaximumFailedPasswordsForWipe(_context);

                    if (expected > 0 && expected != current)
                        if (!DevicePolicyAdmin.setMaximumFailedPasswordsForWipe(_context, expected))
                            throw new Exception("Не удалось установить к-во попыток входа до сброса устройства");
                    break;
                }

                case "USES_POLICY_AUTO_TIME": {
                    if (TextUtils.isEmpty(policySet.PolicyParam))
                        throw new Exception("Параметры не заданы");

                    boolean expected = Boolean.parseBoolean(policySet.PolicyParam);
                    boolean current = DevicePolicyAdmin.getAutoTimeRequired(_context);

                    if (expected != current)
                        if (!DevicePolicyAdmin.setAutoTimeRequired(_context, expected))
                            throw new Exception("Не удалось установить использование времени из сети");
                    break;
                }

                case "USES_POLICY_MUTE_VOLUME": {
                    if (TextUtils.isEmpty(policySet.PolicyParam))
                        throw new Exception("Параметры не заданы");

                    boolean expected = Boolean.parseBoolean(policySet.PolicyParam);
                    boolean current = DevicePolicyAdmin.isMasterVolumeMuted(_context);

                    if (expected != current)
                        if (!DevicePolicyAdmin.setMasterVolumeMuted(_context, expected))
                            throw new Exception("Не удалось установить политику использования динамика");
                    break;
                }

                //Extra action
                case "USES_POLICY_FORCE_REBOOT": {
                    if (!DevicePolicyAdmin.reboot(_context))
                        throw new Exception("Не удалось перегрузить устройство");
                    break;
                }

                //Extra action
                case "USES_POLICY_REMOVE_ACTIVE_ADMIN": {
                    if (!DevicePolicyAdmin.removeActiveAdmin(_context))
                        throw new Exception("Не удалось удалить текущего администратора");
                    break;
                }

                default:
                    throw new Exception("Политика не найдена");
            }

            result.BoolRes = true;
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
            result.ErrorRes = ex.getMessage();
            result.BoolRes = false;
        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String[] getSecurityLog() {
        try {
            List<SecurityLog.SecurityEvent> securityLogs = DevicePolicyAdmin.retrieveSecurityLogs(_context);

            if (securityLogs == null || securityLogs.size() == 0)
                return null;

            String[] result = new String[securityLogs.size()];
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            for (int i = 0; i < securityLogs.size(); i++) {
                SecurityLog.SecurityEvent event = securityLogs.get(i);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(event.getTimeNanos());
                String dt = formatter.format(calendar.getTime());
                result[i] = dt + " " + event.getData() + " " + event.toString();
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }

        return null;
    }
}
