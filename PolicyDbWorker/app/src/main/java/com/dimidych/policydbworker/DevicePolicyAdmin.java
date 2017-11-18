package com.dimidych.policydbworker;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SecurityLog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ProxyInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

public class DevicePolicyAdmin {
    private static final String LOG_TAG = "DevicePolicyAdmin";

    public static void requestAdminService(Activity activity, ComponentName devicePolicyAdmin) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, devicePolicyAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "admin extra app");
        activity.startActivityForResult(intent, 0);
    }

    public static boolean turnOnOffCamera(Context context, boolean enable) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            boolean cameraStatus = devicePolicyManager.getCameraDisabled(devicePolicyAdmin);

            if (cameraStatus && enable) {
                devicePolicyManager.setCameraDisabled(devicePolicyAdmin, false);
                Log.d(LOG_TAG, "Камера включена");
            } else {
                devicePolicyManager.setCameraDisabled(devicePolicyAdmin, true);
                Log.d(LOG_TAG, "Камера отключена");
            }

            return true;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка включения камеры. " + ex.getMessage());
        }

        return false;
    }

    public static long getPasswordExpirationPeriod(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            return devicePolicyManager.getPasswordExpiration(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки периода действия пароля. " + ex.getMessage());
        }

        return 10000;
    }

    public static boolean setPasswordExpirationTimeout(Context context, long period) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            devicePolicyManager.setPasswordExpirationTimeout(devicePolicyAdmin, period);
            return true;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки таймаута смены пароля. " + ex.getMessage());
        }

        return false;
    }

    public static long getPasswordExpirationTimeout(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            return devicePolicyManager.getPasswordExpirationTimeout(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки таймаута смены пароля. " + ex.getMessage());
        }

        return 10000;
    }

    public static boolean setPasswordQuality(Context context, int minLength, int minUpperCaseLetters,
                                          boolean isAlphaNumeric, int minNumeric, int minLetter) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (minLength > 0)
                devicePolicyManager.setPasswordMinimumLength(devicePolicyAdmin, minLength);

            if (minUpperCaseLetters > 0)
                devicePolicyManager.setPasswordMinimumUpperCase(devicePolicyAdmin, minUpperCaseLetters);

            if (minNumeric > 0)
                devicePolicyManager.setPasswordMinimumNumeric(devicePolicyAdmin, minNumeric);

            if (minLetter > 0)
                devicePolicyManager.setPasswordMinimumLetters(devicePolicyAdmin, minLetter);

            if (isAlphaNumeric)
                devicePolicyManager.setPasswordQuality(devicePolicyAdmin, devicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);

            return true;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки качества пароля. " + ex.getMessage());
        }

        return false;
    }

    public static boolean checkPasswordQuality(Context context, int minLength, int minUpperCaseLetters,
                                               boolean isAlphaNumeric, int minNumeric, int minLetter) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            int needCounter = 0, factCounter = 0;

            if (minLength > 0) {
                needCounter++;

                if (devicePolicyManager.getPasswordMinimumLength(devicePolicyAdmin) >= minLength)
                    factCounter++;
            }

            if (minUpperCaseLetters > 0) {
                needCounter++;

                if (devicePolicyManager.getPasswordMinimumUpperCase(devicePolicyAdmin) >= minUpperCaseLetters)
                    factCounter++;
            }

            if (minNumeric > 0) {
                needCounter++;

                if (devicePolicyManager.getPasswordMinimumNumeric(devicePolicyAdmin) >= minNumeric)
                    factCounter++;
            }

            if (minLetter > 0) {
                needCounter++;

                if (devicePolicyManager.getPasswordMinimumLetters(devicePolicyAdmin) >= minLetter)
                    factCounter++;
            }

            if (isAlphaNumeric) {
                needCounter++;

                if (devicePolicyManager.getPasswordQuality(devicePolicyAdmin) == devicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC)
                    factCounter++;
            }

            return needCounter == factCounter;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки качества пароля. " + ex.getMessage());
        }

        return false;
    }

    public static boolean wipeData(Context context, boolean wipeExtStorage, boolean wipeFactoryReset) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (wipeExtStorage)
                devicePolicyManager.wipeData(devicePolicyManager.WIPE_EXTERNAL_STORAGE);

            if (wipeFactoryReset)
                devicePolicyManager.wipeData(devicePolicyManager.WIPE_RESET_PROTECTION_DATA);

            return true;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки стирания пользовательских данных. " + ex.getMessage());
        }

        return false;
    }

    public static int getMaximumFailedPasswordsForWipe(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            return devicePolicyManager.getMaximumFailedPasswordsForWipe(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки к-ва ошибочных вводов пароля перед сбросом устройства. " + ex.getMessage());
        }

        return -1;
    }

    public static boolean setMaximumFailedPasswordsForWipe(Context context, int passwordTryCount) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            devicePolicyManager.setMaximumFailedPasswordsForWipe(devicePolicyAdmin, passwordTryCount);
            return true;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки к-ва ошибочных вводов пароля перед сбросом устройства. " + ex.getMessage());
        }

        return false;
    }

    public static long getMaximumTimeToLock(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            return devicePolicyManager.getMaximumTimeToLock(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки времени до блокировки. " + ex.getMessage());
        }

        return 10000;
    }

    public static void setMaximumTimeToLock(Context context, long period) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            devicePolicyManager.setMaximumTimeToLock(devicePolicyAdmin, period);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки времени до блокировки. " + ex.getMessage());
        }
    }

    public static boolean getStorageEncryption(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            return devicePolicyManager.getStorageEncryption(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки шифрования. " + ex.getMessage());
        }

        return false;
    }

    public static boolean setStorageEncryption(Context context, boolean isEncrypted) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            devicePolicyManager.setStorageEncryption(devicePolicyAdmin, isEncrypted);
            return true;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки шифрования. " + ex.getMessage());
        }

        return false;
    }

    public static boolean getScreenCaptureDisabled(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return devicePolicyManager.getScreenCaptureDisabled(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки запрета скриншотов. " + ex.getMessage());
        }

        return false;
    }

    public static void setScreenCaptureDisabled(Context context, boolean disabled) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                devicePolicyManager.setScreenCaptureDisabled(devicePolicyAdmin, disabled);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки запрета скриншотов. " + ex.getMessage());
        }
    }

    public static String getWifiMacAddress(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return devicePolicyManager.getWifiMacAddress(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки Wifi Mac адреса. " + ex.getMessage());
        }

        return "";
    }

    public static boolean getAutoTimeRequired(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return devicePolicyManager.getAutoTimeRequired();
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки автоматической настройки времени. " + ex.getMessage());
        }

        return false;
    }

    public static boolean setAutoTimeRequired(Context context, boolean required) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                devicePolicyManager.setAutoTimeRequired(devicePolicyAdmin, required);

            return true;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки автоматической настройки времени. " + ex.getMessage());
        }

        return false;
    }

    public static boolean getBluetoothContactSharingDisabled(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                return devicePolicyManager.getBluetoothContactSharingDisabled(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки передачи контактов через Bluetooth. " + ex.getMessage());
        }

        return false;
    }

    public static void setBluetoothContactSharingDisabled(Context context, boolean disabled) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                devicePolicyManager.setBluetoothContactSharingDisabled(devicePolicyAdmin, disabled);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки передачи контактов через Bluetooth. " + ex.getMessage());
        }
    }

    public static List<byte[]> getInstalledCaCerts(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return devicePolicyManager.getInstalledCaCerts(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки списка сертификатов. " + ex.getMessage());
        }

        return null;
    }

    public static int getKeyguardDisabledFeatures(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                return devicePolicyManager.getKeyguardDisabledFeatures(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки настроек ввода. " + ex.getMessage());
        }

        return -1;
    }

    public static void setKeyguardDisabledFeatures(Context context, int key) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            //KEYGUARD_DISABLE_TRUST_AGENTS, KEYGUARD_DISABLE_FINGERPRINT, KEYGUARD_DISABLE_UNREDACTED_NOTIFICATIONS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                devicePolicyManager.setKeyguardDisabledFeatures(devicePolicyAdmin, key);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки настроек ввода. " + ex.getMessage());
        }

    }

    public static boolean hasCaCertInstalled(Context context, byte[] certBuffer) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return devicePolicyManager.hasCaCertInstalled(devicePolicyAdmin, certBuffer);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки установленного сертификата. " + ex.getMessage());
        }

        return false;
    }

    public static boolean installCaCert(Context context, byte[] certBuffer) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return devicePolicyManager.installCaCert(devicePolicyAdmin, certBuffer);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки сертификата. " + ex.getMessage());
        }

        return false;
    }

    public static boolean installKeyPair(Context context, PrivateKey privKey, Certificate cert, String alias) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return devicePolicyManager.installKeyPair(devicePolicyAdmin, privKey, cert, alias);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки цепочки сертификатов. " + ex.getMessage());
        }

        return false;
    }

    public static boolean isActivePasswordSufficient(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            return devicePolicyManager.isActivePasswordSufficient();
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки соответствия пароля. " + ex.getMessage());
        }

        return false;
    }

    public static boolean isAdminActive(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            return devicePolicyManager.isAdminActive(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки администратора. " + ex.getMessage());
        }

        return false;
    }

    public static boolean isMasterVolumeMuted(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return devicePolicyManager.isMasterVolumeMuted(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки отключения звука. " + ex.getMessage());
        }

        return false;
    }

    public static boolean setMasterVolumeMuted(Context context, boolean on) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                devicePolicyManager.setMasterVolumeMuted(devicePolicyAdmin, on);

            return true;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка блокировки устройства. " + ex.getMessage());
        }

        return false;
    }

    public static boolean isSecurityLoggingEnabled(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                return devicePolicyManager.isSecurityLoggingEnabled(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки ведения лога безопасности. " + ex.getMessage());
        }

        return false;
    }

    public static boolean lockNow(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            devicePolicyManager.lockNow();
            return true;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка блокировки устройства. " + ex.getMessage());
        }

        return false;
    }

    public static boolean reboot(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                devicePolicyManager.reboot(devicePolicyAdmin);

            return true;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка перезагрузки устройства. " + ex.getMessage());
        }

        return false;
    }

    public static boolean removeActiveAdmin(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);
            devicePolicyManager.removeActiveAdmin(devicePolicyAdmin);

            return true;
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка удаления текущего администратора. " + ex.getMessage());
        }

        return false;
    }

    public static boolean removeKeyPair(Context context, String alias) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                return devicePolicyManager.removeKeyPair(devicePolicyAdmin, alias);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка удаления цепочки сертификатов. " + ex.getMessage());
        }

        return false;
    }

    public static boolean resetPassword(Context context, String password, int flags) {
        try {// RESET_PASSWORD_REQUIRE_ENTRY,  RESET_PASSWORD_DO_NOT_ASK_CREDENTIALS_ON_BOOT
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            return devicePolicyManager.resetPassword(password, flags);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка сброса пароля. " + ex.getMessage());
        }

        return false;
    }

    public static List<SecurityLog.SecurityEvent> retrieveSecurityLogs(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                return devicePolicyManager.retrieveSecurityLogs(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка получения логов безопасности. " + ex.getMessage());
        }

        return null;
    }

    public static void setApplicationRestrictions(Context context, String packageName, Bundle settings) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                devicePolicyManager.setApplicationRestrictions(devicePolicyAdmin, packageName, settings);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки ограничений для приложения. " + ex.getMessage());
        }
    }

    public static Bundle getApplicationRestrictions(Context context, String packageName) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return devicePolicyManager.getApplicationRestrictions(devicePolicyAdmin, packageName);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка получения ограничений для приложения. " + ex.getMessage());
        }

        return null;
    }

    public static boolean setApplicationHidden(Context context, String packageName, boolean hidden) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return devicePolicyManager.setApplicationHidden(devicePolicyAdmin, packageName, hidden);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка скрытия приложения. " + ex.getMessage());
        }

        return false;
    }

    public static boolean isApplicationHidden(Context context, String packageName) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return devicePolicyManager.isApplicationHidden(devicePolicyAdmin, packageName);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка проверки скрытия приложения. " + ex.getMessage());
        }

        return false;
    }

    public static void setAlwaysOnVpnPackage(Context context, String vpnPackage, boolean lockdownEnabled) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                devicePolicyManager.setAlwaysOnVpnPackage(devicePolicyAdmin, vpnPackage, lockdownEnabled);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки Vpn для приложения. " + ex.getMessage());
        }
    }

    public static String getAlwaysOnVpnPackage(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                return devicePolicyManager.getAlwaysOnVpnPackage(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка получения Vpn для приложения. " + ex.getMessage());
        }

        return "";
    }

    public static void setDeviceOwnerLockScreenInfo(Context context, CharSequence info) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                devicePolicyManager.setDeviceOwnerLockScreenInfo(devicePolicyAdmin, info);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки экрана блокировки. " + ex.getMessage());
        }
    }

    public static void setGlobalSetting(Context context, String setting, String value) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                devicePolicyManager.setGlobalSetting(devicePolicyAdmin, setting, value);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки глобальных настроек. " + ex.getMessage());
        }
    }

    public static void setRecommendedGlobalProxy(Context context, ProxyInfo proxyInfo) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                devicePolicyManager.setRecommendedGlobalProxy(devicePolicyAdmin, proxyInfo);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка установки proxy. " + ex.getMessage());
        }
    }

    public static void uninstallAllUserCaCerts(Context context) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                devicePolicyManager.uninstallAllUserCaCerts(devicePolicyAdmin);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка удаления всех пользовательских сертификатов. " + ex.getMessage());
        }
    }

    public static void uninstallCaCert(Context context, byte[] certBuffer) {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName devicePolicyAdmin = new ComponentName(context.getApplicationContext(), PolicyAdminReceiver.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                devicePolicyManager.uninstallCaCert(devicePolicyAdmin, certBuffer);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Ошибка удаления сертификата. " + ex.getMessage());
        }
    }
}
