package com.dimidych.policydbworker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.dimidych.policydbworker.mvp.ICheckPolicyModelOps;
import com.dimidych.policydbworker.mvp.ICheckPolicyPresenterRequiredOps;
import com.dimidych.policydbworker.mvp.IEventLogModelOps;
import com.dimidych.policydbworker.mvp.IEventLogPresenterRequiredOps;
import com.dimidych.policydbworker.mvp.IGetCertModelOps;
import com.dimidych.policydbworker.mvp.IGetCertPresenterRequiredOps;
import com.dimidych.policydbworker.mvp.INetworkModelOps;
import com.dimidych.policydbworker.mvp.INetworkPresenterRequiredOps;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class DbWorker extends SQLiteOpenHelper
        implements INetworkModelOps, IGetCertModelOps, ICheckPolicyModelOps, IEventLogModelOps {
    private static final String LOG_TAG = "DbWorker";
    public Context Context = null;
    private SQLiteDatabase CurrentDb = null;
    private INetworkPresenterRequiredOps _networkPresenter;
    private IGetCertPresenterRequiredOps _getCertPresenter;
    private ICheckPolicyPresenterRequiredOps _checkPolicyPresenter;
    private IEventLogPresenterRequiredOps _eventLogPresenter;

    private static final String PolicySetIdFld = "policy_set_id";
    private static final String PolicyIdFld = "policy_id";
    private static final String LoginIdFld = "login_id";
    private static final String GroupIdFld = "group_id";
    private static final String PolicyNameFld = "policy_name";
    private static final String PolicyInstructionFld = "policy_instruction";
    private static final String PlatformIdFld = "platform_id";
    private static final String PolicyParamFld = "policy_param";

    public static final String EventLogIdFld = "event_log_id";
    public static final String EventLogDateFld = "event_log_date";
    private static final String DocumentIdFld = "document_id";
    private static final String MessageFld = "message";
    private static final String LoginFld = "login";
    private static final String EventNameFld = "event_name";

    public DbWorker(Context context) {
        super(context, "policySvc", null, 1);
        Context = context;
        CurrentDb = getWritableDatabase();
    }

    public DbWorker(Context context, INetworkPresenterRequiredOps networkPresenter) {
        this(context);
        _networkPresenter = networkPresenter;
    }

    public DbWorker(Context context, IGetCertPresenterRequiredOps getCertPresenter) {
        this(context);
        _getCertPresenter = getCertPresenter;
    }

    public DbWorker(Context context, ICheckPolicyPresenterRequiredOps checkPolicyPresenter) {
        this(context);
        _checkPolicyPresenter = checkPolicyPresenter;
    }

    public DbWorker(Context context, IEventLogPresenterRequiredOps eventLogPresenter) {
        this(context);
        _eventLogPresenter = eventLogPresenter;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(LOG_TAG, "Пытаемся создать БД POLICYSVC");
            db.beginTransactionNonExclusive();

            try {
                db.execSQL("CREATE TABLE CONNECTION_TBL (host_ip TEXT NOT NULL, host_port TEXT NOT NULL, cert TEXT NOT NULL);");
                Log.d(LOG_TAG, "Таблица CONNECTION_TBL создана");
                db.execSQL("CREATE TABLE POLICY_SET_TBL (policy_set_id INTEGER PRIMARY KEY, " +
                        "policy_id INTEGER, login_id INTEGER, group_id INTEGER, login TEXT, " +
                        "policy_name TEXT, policy_instruction TEXT, platform_id INTEGER, policy_param TEXT);");
                Log.d(LOG_TAG, "Таблица POLICY_SET_TBL создана");
                db.execSQL("CREATE TABLE EVENT_LOG_TBL (event_log_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "event_log_date DATETIME NOT NULL DEFAULT(DATETIME('now', 'localtime')), " +
                        "event_name TEXT,document_id INTEGER, message TEXT, login TEXT);");
                Log.d(LOG_TAG, "Таблица EVENT_LOG_TBL создана");
                db.setTransactionSuccessful();
            } catch (Exception ex) {
                String strErr = "Ошибка транзакции создания БД - " + ex.getMessage();
                Log.e(LOG_TAG, strErr);
            } finally {
                db.endTransaction();
            }

            //////////////////////////////// Values set //////////////////////////////////////
            CurrentDb = db;
            ArrayList<HashMap<String, String>> paramTypeValueCollection = new ArrayList<HashMap<String, String>>();
            paramTypeValueCollection.add(new HashMap<String, String>() {{
                put("host_ip", "10.0.2.2");
            }});
            paramTypeValueCollection.add(new HashMap<String, String>() {{
                put("host_port", "8732");
            }});
            paramTypeValueCollection.add(new HashMap<String, String>() {{
                put("cert", "test");
            }});

            makePackageInsert("CONNECTION_TBL", paramTypeValueCollection);
            paramTypeValueCollection.clear();
            Log.d(LOG_TAG, "Добавляем начальные данные в таблицу CONNECTION_TBL");
            Log.d(LOG_TAG, "БД POLICYSVC создана");
        } catch (Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
            return;
        } finally {
            onSetLog("БД POLICYSVC создана", "Success", -1);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    protected void finalize() {
        close();
    }

    public void onDestroy() {
        close();
    }

    @Override
    public void onSetLog(String message, String eventName, long documentId) {
        setEventLog(new EventLogDataContract(documentId, message, "", eventName));
    }

    public boolean makePackageInsert(String strTableName, ArrayList<HashMap<String, String>> paramTypeValueCollection) {
        boolean result = false;

        try {
            if (CurrentDb == null)
                throw new Exception("БД не существует");

            if (strTableName.trim().equalsIgnoreCase(""))
                throw new Exception("Не указано имя таблицы");

            if (paramTypeValueCollection == null || paramTypeValueCollection.isEmpty())
                throw new Exception("Параметры не указаны");

            ContentValues cv = new ContentValues();

            for (int i = 0; i < paramTypeValueCollection.size(); i++) {
                try {
                    HashMap<String, String> paramAttr = paramTypeValueCollection.get(i);

                    if (paramAttr == null || paramAttr.isEmpty())
                        continue;

                    Object[] keyArr = paramAttr.keySet().toArray();

                    for (int j = 0; j < keyArr.length; j++) {
                        String key = (String) (keyArr[j]);
                        cv.put(key, paramAttr.get(key));
                    }

                    CurrentDb.insert(strTableName, null, cv);
                } catch (Exception ex) {
                    Log.d(LOG_TAG, ex.getMessage());
                }
            }

            result = true;
        } catch (Exception ex) {
            String strErr = " Ошибка пакетной вставки - " + ex.getMessage();
            Log.e(LOG_TAG, strErr);
            onSetLog(LOG_TAG + strErr, "Error", -1);
            return false;
        }

        return result;
    }

    public Entry<String, String> getConnectionSettings() {
        Cursor reader = null;

        try {
            String query = "select host_ip,host_port from CONNECTION_TBL";
            reader = CurrentDb.rawQuery(query, new String[]{});

            if (reader == null)
                throw new Exception("IP и порт хоста не указаны");

            if (reader.moveToFirst()) {
                final String serverIpAddress = reader.getString(0);
                final String serverPort = reader.getString(1);
                return new AbstractMap.SimpleEntry<String, String>(serverIpAddress, serverPort);
            }
        } catch (Exception ex) {
            String error = " Ошибка получения настроек подключения - " + ex.getMessage();
            _networkPresenter.onError(error);
            Log.e(LOG_TAG, error);
            onSetLog(LOG_TAG + error, "Error", -1);
        } finally {
            if (reader != null && !reader.isClosed())
                reader.close();
        }

        return null;
    }

    public boolean updateConnectionSettings(String ipAddress, String port) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("host_ip", ipAddress);
            cv.put("host_port", port);
            return CurrentDb.update("CONNECTION_TBL", cv, null, null) > 0;
        } catch (Exception ex) {
            String error = " Ошибка изменения настроек соединения - " + ex.getMessage();
            _networkPresenter.onError(error);
            Log.e(LOG_TAG, error);
            onSetLog(LOG_TAG + error, "Error", -1);
        } finally {
            _networkPresenter.onUpdateConnectionSettings(ipAddress, port);
        }

        return false;
    }

    public String getCertificate() {
        Cursor reader = null;

        try {
            String query = "select cert from CONNECTION_TBL";
            reader = CurrentDb.rawQuery(query, new String[]{});

            if (reader.moveToFirst())
                return reader.getString(0);
        } catch (Exception ex) {
            String error = " Ошибка получения сертификата - " + ex.getMessage();
            _getCertPresenter.onError(error);
            Log.e(LOG_TAG, error);
            onSetLog(LOG_TAG + error, "Error", -1);
        } finally {
            if (reader != null && !reader.isClosed())
                reader.close();
        }

        return null;
    }

    public boolean updateCertificate(String certifiate) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("cert", certifiate);
            return CurrentDb.update("CONNECTION_TBL", cv, null, null) > 0;
        } catch (Exception ex) {
            String error = " Ошибка изменения набора политик - " + ex.getMessage();
            _getCertPresenter.onError(error);
            Log.e(LOG_TAG, error);
            onSetLog(LOG_TAG + error, "Error", -1);
        } finally {
            _getCertPresenter.onUpdateCertificate(certifiate);
        }

        return false;
    }

    public boolean addPolicySetToDb(PolicySetDataContract policySet) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(PolicySetIdFld, policySet.PolicySetId);
            cv.put(PolicyIdFld, policySet.PolicyId);
            cv.put(LoginIdFld, policySet.LoginId);
            cv.put(GroupIdFld, policySet.GroupId);
            cv.put(PolicyNameFld, policySet.PolicyName);
            cv.put(PolicyInstructionFld, policySet.PolicyInstruction);
            cv.put(PlatformIdFld, policySet.PlatformId);
            cv.put(PolicyParamFld, policySet.PolicyParam);
            return CurrentDb.insert("POLICY_SET_TBL", null, cv) > 0;
        } catch (Exception ex) {
            String error = " Ошибка добавления набора политик - " + ex.getMessage();
            _checkPolicyPresenter.onError(error);
            Log.e(LOG_TAG, error);
            onSetLog(LOG_TAG + error, "Error", -1);
        } finally {
            _checkPolicyPresenter.onAddPolicySetToDb(policySet);
        }

        return false;
    }

    public boolean updatePolicySet(PolicySetDataContract servicePolicySet, PolicySetDataContract dbPolicySet) {
        try {
            ContentValues cv = new ContentValues();

            if (servicePolicySet.PolicyId != dbPolicySet.PolicyId)
                cv.put(DbWorker.PolicyIdFld, servicePolicySet.PolicyId);

            if (servicePolicySet.LoginId != dbPolicySet.LoginId)
                cv.put(DbWorker.LoginIdFld, servicePolicySet.LoginId);

            if (servicePolicySet.GroupId != dbPolicySet.GroupId)
                cv.put(DbWorker.GroupIdFld, servicePolicySet.GroupId);

            if (servicePolicySet.PolicyParam != dbPolicySet.PolicyParam)
                cv.put(DbWorker.PolicyParamFld, servicePolicySet.PolicyParam);

            if (servicePolicySet.PolicyName != dbPolicySet.PolicyName)
                cv.put(DbWorker.PolicyNameFld, servicePolicySet.PolicyName);

            if (servicePolicySet.PolicyInstruction != dbPolicySet.PolicyInstruction)
                cv.put(DbWorker.PolicyInstructionFld, servicePolicySet.PolicyInstruction);

            if (servicePolicySet.PlatformId != dbPolicySet.PlatformId)
                cv.put(DbWorker.PlatformIdFld, servicePolicySet.PlatformId);

            if (cv.size() < 1)
                return true;

            return CurrentDb.update("POLICY_SET_TBL", cv, DbWorker.PolicySetIdFld + "=?",
                    new String[]{"" + servicePolicySet.PolicySetId}) > 0;
        } catch (Exception ex) {
            String error = " Ошибка изменения набора политик - " + ex.getMessage();
            _checkPolicyPresenter.onError(error);
            Log.e(LOG_TAG, error);
            onSetLog(LOG_TAG + error, "Error", -1);
        } finally {
            _checkPolicyPresenter.onUpdatePolicySet(servicePolicySet, dbPolicySet);
        }

        return false;
    }

    public PolicySetDataContract getSinglePolicySetFromDb(long policyId) {
        Cursor reader = null;

        try {
            String query = "select policy_set_id, policy_id, login_id, group_id, login, policy_name," +
                    "policy_instruction, platform_id, policy_param from POLICY_SET_TBL where policy_id=?";
            String[] queryParams = new String[]{"" + policyId};
            reader = CurrentDb.rawQuery(query, queryParams);

            if (reader == null)
                return null;

            PolicySetDataContract result = new PolicySetDataContract();

            if (reader.moveToFirst()) {
                result.PolicySetId = reader.getLong(0);
                result.PolicyId = reader.getInt(1);
                result.LoginId = reader.getLong(2);
                result.GroupId = reader.getInt(3);
                result.Selected = true;
                result.PolicyName = reader.getString(5);
                result.PolicyInstruction = reader.getString(6);
                result.PlatformId = reader.getShort(7);
                result.PolicyParam = reader.getString(8);
                return result;
            }
        } catch (Exception ex) {
            String error = " Ошибка получения набора политик - " + ex.getMessage();
            _checkPolicyPresenter.onError(error);
            Log.e(LOG_TAG, error);
            onSetLog(LOG_TAG + error, "Error", -1);
        } finally {
            if (reader != null && !reader.isClosed())
                reader.close();
        }

        return null;
    }

    public PolicySetDataContract[] getPolicySetFromDb() {
        Cursor reader = null;

        try {
            String query = "select policy_set_id, policy_id, login_id, group_id, login, " +
                    "policy_name, policy_instruction, platform_id, policy_param from POLICY_SET_TBL";
            reader = CurrentDb.rawQuery(query, new String[]{});

            if (reader == null)
                throw new Exception("Наборы политик не обнаружены");

            ArrayList<PolicySetDataContract> policySetLst = new ArrayList<>();
            Map<String, Integer> fieldsAndIndexes = new HashMap<>();

            if (reader.moveToFirst())
                do {
                    PolicySetDataContract policySet = new PolicySetDataContract();

                    if (!fieldsAndIndexes.containsKey(PolicySetIdFld))
                        fieldsAndIndexes.put(PolicySetIdFld, reader.getColumnIndex(PolicySetIdFld));

                    policySet.PolicySetId = reader.getInt(fieldsAndIndexes.get(PolicySetIdFld));

                    if (!fieldsAndIndexes.containsKey(PolicyIdFld))
                        fieldsAndIndexes.put(PolicyIdFld, reader.getColumnIndex(PolicyIdFld));

                    policySet.PolicyId = reader.getInt(fieldsAndIndexes.get(PolicyIdFld));

                    if (!fieldsAndIndexes.containsKey(LoginIdFld))
                        fieldsAndIndexes.put(LoginIdFld, reader.getColumnIndex(LoginIdFld));

                    policySet.LoginId = reader.getInt(fieldsAndIndexes.get(LoginIdFld));

                    if (!fieldsAndIndexes.containsKey(GroupIdFld))
                        fieldsAndIndexes.put(GroupIdFld, reader.getColumnIndex(GroupIdFld));

                    policySet.GroupId = reader.getInt(fieldsAndIndexes.get(GroupIdFld));

                    if (!fieldsAndIndexes.containsKey(PolicyNameFld))
                        fieldsAndIndexes.put(PolicyNameFld, reader.getColumnIndex(PolicyNameFld));

                    policySet.PolicyName = reader.getString(fieldsAndIndexes.get(PolicyNameFld));

                    if (!fieldsAndIndexes.containsKey(PolicyInstructionFld))
                        fieldsAndIndexes.put(PolicyInstructionFld, reader.getColumnIndex(PolicyInstructionFld));

                    policySet.PolicyInstruction = reader.getString(fieldsAndIndexes.get(PolicyInstructionFld));

                    if (!fieldsAndIndexes.containsKey(PlatformIdFld))
                        fieldsAndIndexes.put(PlatformIdFld, reader.getColumnIndex(PlatformIdFld));

                    policySet.PlatformId = reader.getShort(fieldsAndIndexes.get(PlatformIdFld));

                    if (!fieldsAndIndexes.containsKey(PolicyParamFld))
                        fieldsAndIndexes.put(PolicyParamFld, reader.getColumnIndex(PolicyParamFld));

                    policySet.PolicyParam = reader.getString(fieldsAndIndexes.get(PolicyParamFld));

                    policySetLst.add(policySet);
                } while (reader.moveToNext());

            PolicySetDataContract[] result = new PolicySetDataContract[policySetLst.size()];
            return policySetLst.toArray(result);
        } catch (Exception ex) {
            String error = " Ошибка получения набора политик - " + ex.getMessage();
            _checkPolicyPresenter.onError(error);
            Log.e(LOG_TAG, error);
            onSetLog(LOG_TAG + error, "Error", -1);
        } finally {
            if (reader != null && !reader.isClosed())
                reader.close();
        }

        return null;
    }

    public EventLogDataContract[] getEventLog(String fromDate, String toDate, long documentId, String eventName) {
        Cursor reader = null;

        try {
            String query = "select event_log_id, event_log_date, event_name, document_id, message, login from EVENT_LOG_TBL ";
            int paramCounter = 0;
            String[] queryParams = new String[4];
            Calendar fromCalendar = Calendar.getInstance();
            Calendar toCalendar = Calendar.getInstance();
            long fromDateInMillis = 0;
            long toDateInMillis = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try {
                fromCalendar.setTime(sdf.parse(fromDate));
                fromDateInMillis = fromCalendar.getTimeInMillis();
            } catch (Exception ex) {
            }

            try {
                toCalendar.setTime(sdf.parse(toDate));
                toDateInMillis = toCalendar.getTimeInMillis();
            } catch (Exception ex) {
            }

            if (fromDateInMillis > 0) {
                query += paramCounter > 0 ? " and event_log_date>=?" : " where event_log_date>=?";
                queryParams[paramCounter] = fromDate;
                paramCounter++;
            }

            if (toDateInMillis > 0 && toDateInMillis > fromDateInMillis) {
                query += paramCounter > 0 ? " and event_log_date<=?" : " where event_log_date<=?";
                queryParams[paramCounter] = toDate;
                paramCounter++;
            }

            if (documentId > 0) {
                query += paramCounter > 0 ? " and document_id=?" : " where document_id=?";
                queryParams[paramCounter] = "" + documentId;
                paramCounter++;
            }

            if (!TextUtils.isEmpty(eventName)) {
                query += paramCounter > 0 ? " and event_name=?" : " where event_name=?";
                queryParams[paramCounter] = eventName;
                paramCounter++;
            }

            reader = CurrentDb.rawQuery(query, queryParams);

            if (reader == null)
                return null;

            if (reader.moveToFirst()) {
                EventLogDataContract[] result = new EventLogDataContract[reader.getCount()];
                int counter = 0;

                do {
                    EventLogDataContract res = new EventLogDataContract();
                    SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.getDefault());
                    res.EventLogDate = formatter.parse(reader.getString(1));
                    res.EventName = reader.getString(2);
                    res.DocumentId = reader.getLong(3);
                    res.Message = reader.getString(4);
                    res.Login = reader.getString(5);
                    result[counter] = res;
                    counter++;
                } while (reader.moveToNext());
            }
        } catch (Exception ex) {
            String error = " Ошибка получения лога событий - " + ex;
            Log.e(LOG_TAG, error);
            onSetLog(LOG_TAG + error, "Error", -1);
        } finally {
            if (reader != null && !reader.isClosed())
                reader.close();
        }

        return null;
    }

    public boolean setEventLog(EventLogDataContract log) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(DocumentIdFld, "" + log.DocumentId);
            cv.put(MessageFld, log.Message);
            cv.put(LoginFld, log.Login);
            cv.put(EventNameFld, log.EventName);
            return CurrentDb.insert("EVENT_LOG_TBL", null, cv) > 0;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Ошибка добавления лога событий - " + ex.getMessage());
        }

        return false;
    }

    public boolean dropEventLogTbl() {
        try {
            return CurrentDb.delete("EVENT_LOG_TBL", null, null) > 0;
        } catch (Exception ex) {
            String error = " Ошибка очистки лога событий - " + ex.getMessage();
            Log.e(LOG_TAG, error);
            onSetLog(LOG_TAG + error, "Error", -1);
        }

        return false;
    }
}
