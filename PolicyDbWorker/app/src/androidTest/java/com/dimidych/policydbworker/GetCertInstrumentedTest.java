package com.dimidych.policydbworker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.dimidych.policydbworker.mvp.ICheckPolicyPresenterRequiredOps;
import com.dimidych.policydbworker.mvp.IGetCertModelOps;
import com.dimidych.policydbworker.mvp.IGetCertPresenterRequiredOps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class GetCertInstrumentedTest {
    private String _ipAddress = "10.0.2.2";
    private String _port = "8732";
    private String _deviceSerial = "cb5a1xnerw";

    @Test
    public void getLoginDevicesAsyncTaskTest() throws Exception {
        Result<LoginDevicesDataContract[]> loginDevices = GetLoginDevicesAsyncTask.getLoginDevicesInfo(
                _ipAddress, _port, _deviceSerial);

        if (!loginDevices.BoolRes || loginDevices.SomeResult == null || loginDevices.SomeResult.length < 1)
            throw new Exception("Ошибка получения информации об авторизации устройства. " + loginDevices.ErrorRes);

        String serviceResult = NetworkWorker.restServiceCall(_ipAddress, _port,
                "/PolicyProjectManagementService/LoginService/GetCertificateRest", loginDevices.SomeResult[0].LoginId);

        if (TextUtils.isEmpty(serviceResult))
            throw new Exception("Ошибка выполнения запроса к службе");

        Gson gson = new Gson();
        Result<String[]> result = gson.fromJson(serviceResult, new TypeToken<Result<String[]>>() {
        }.getType());

        Assert.assertTrue(result.BoolRes);
        Assert.assertTrue(result.SomeResult != null);
        Assert.assertTrue(result.ErrorRes == null || result.ErrorRes == "");
    }

    @Test //(expected = NullPointerException.class)
    public void updateCertTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        IGetCertModelOps model = new DbWorker(appContext, new GetCertPresenterMock());
        boolean result = model.updateCertificate("fake_cert");
        Assert.assertTrue(result);
    }

    @Test
    public void getPolicySetFromServiceTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DevicePolicyAdmin.requestAdminService(new Activity(), new ComponentName(appContext,
                PolicyAdminReceiver.class));

        CheckPolicyUtils checkPolicyUtils = new CheckPolicyUtils(appContext);
        Result<ArrayList<Map.Entry<PolicySetDataContract, String>>> failedPolicies = new Result<>();
        failedPolicies.SomeResult = new ArrayList<>();
        Result<PolicySetDataContract[]> policySetServiceResult = GetPolicySetAsyncTask.getPolicySet(
                _ipAddress, _port, _deviceSerial);
        boolean wrightServiceStmt = (policySetServiceResult.BoolRes && TextUtils.isEmpty(policySetServiceResult.ErrorRes)
                && policySetServiceResult.SomeResult != null || policySetServiceResult.SomeResult.length > 0);
        Assert.assertTrue("Сервис не вернул результат. " + policySetServiceResult.ErrorRes, wrightServiceStmt);
        DbWorker _dbWrkInst = new DbWorker(appContext, new CheckPolicyPresenterMock());

        for (PolicySetDataContract servicePolicySet : policySetServiceResult.SomeResult) {
            PolicySetDataContract dbPolicySet = _dbWrkInst.getSinglePolicySetFromDb(servicePolicySet.PolicyId);

            if (dbPolicySet == null) {
                Assert.assertTrue("Не удалось вставить набор политик с ид " + servicePolicySet.PolicySetId,
                        _dbWrkInst.addPolicySetToDb(servicePolicySet));
            } else {
                Assert.assertTrue("Не удалось изменить набор политик с ид " + servicePolicySet.PolicySetId,
                        _dbWrkInst.updatePolicySet(servicePolicySet, dbPolicySet));
            }

            Result<String> checkPolicyResult = checkPolicyUtils.checkPolicy(servicePolicySet);

            if (!checkPolicyResult.BoolRes) {
                failedPolicies.SomeResult.add(new AbstractMap.SimpleEntry<>(servicePolicySet, checkPolicyResult.ErrorRes));
                Assert.assertTrue("Ошибка записи лога", _dbWrkInst.setEventLog(new EventLogDataContract(-1,
                        "Policy " + servicePolicySet.PolicyName + " was not set with error " + checkPolicyResult.ErrorRes, "", "Policy set error")));
            }
        }

        failedPolicies.BoolRes = true;
    }

    @Test
    public void getPolicySetFromServiceAtLoaderTest() {
        Result<ArrayList<Map.Entry<PolicySetDataContract, String>>> failedPolicies = new Result<>();

        try {
            Context _context = InstrumentationRegistry.getTargetContext();
            DbWorker _dbWrkInst = new DbWorker(_context, new CheckPolicyPresenterMock());
            String[] devinfo = Utils.getDeviceInfoAndPing(_context);

            if (devinfo == null || devinfo.length != 3)
                throw new Exception("Ошибка сети");

            failedPolicies.SomeResult = new ArrayList<>();
            //policies for device from service
            Result<PolicySetDataContract[]> policySetServiceResult = GetPolicySetAsyncTask.getPolicySet(
                    devinfo[0], devinfo[1], devinfo[2]);

            if (!policySetServiceResult.BoolRes || !TextUtils.isEmpty(policySetServiceResult.ErrorRes)
                    || policySetServiceResult.SomeResult == null || policySetServiceResult.SomeResult.length < 1)
                throw new Exception("Сервис не вернул результат. " + policySetServiceResult.ErrorRes);

            //remove orphan policies from local db
            PolicySetDataContract[] policiesFromDb = _dbWrkInst.getPolicySetFromDb();

            if (policiesFromDb != null && policiesFromDb.length > 0) {
                List<Integer> policiesToDelete = new ArrayList<>();

                for (PolicySetDataContract dbPolicySet : policiesFromDb) {
                    int counter = 0;

                    for (PolicySetDataContract servicePolicySet : policySetServiceResult.SomeResult)
                        if (servicePolicySet.PolicyId == dbPolicySet.PolicyId) {
                            counter++;
                            break;
                        }

                    if (counter == 0 && !policiesToDelete.contains(dbPolicySet.PolicyId))
                        policiesToDelete.add(dbPolicySet.PolicyId);
                }

                if (policiesToDelete.size() > 0)
                    for (int policyId : policiesToDelete)
                        _dbWrkInst.deletePolicySetFromDb(policyId);
            }

            // first : try to add or update policies from service intolocal db
            // then : try to check policies. failed policies will be thrown as result with log record
            //CheckPolicyUtils checkPolicyUtils = new CheckPolicyUtils(_context);

            for (PolicySetDataContract servicePolicySet : policySetServiceResult.SomeResult) {
                PolicySetDataContract dbPolicySet = _dbWrkInst.getSinglePolicySetFromDb(servicePolicySet.PolicyId);

                if (dbPolicySet == null) {
                    if (!_dbWrkInst.addPolicySetToDb(servicePolicySet))
                        throw new Exception("Не удалось вставить набор политик с ид " + servicePolicySet.PolicySetId);
                } else {
                    if (!_dbWrkInst.updatePolicySet(servicePolicySet, dbPolicySet))
                        throw new Exception("Не удалось изменить набор политик с ид " + servicePolicySet.PolicySetId);
                }

                //Result<String> checkPolicyResult = checkPolicyUtils.checkPolicy(servicePolicySet);
                //servicePolicySet.Selected = checkPolicyResult.BoolRes;
                failedPolicies.SomeResult.add(new AbstractMap.SimpleEntry<>(servicePolicySet, ""));
            }

            failedPolicies.BoolRes = true;
        } catch (Exception ex) {
            failedPolicies.ErrorRes = "Server Policy set loader error. " + ex.getMessage();
        }
    }

    class CheckPolicyPresenterMock implements ICheckPolicyPresenterRequiredOps {
        @Override
        public void onAddPolicySetToDb(PolicySetDataContract policySet) {
        }

        @Override
        public void onUpdatePolicySet(PolicySetDataContract servicePolicySet, PolicySetDataContract dbPolicySet) {
        }

        @Override
        public void onError(String errorMsg) {
        }
    }

    class GetCertPresenterMock implements IGetCertPresenterRequiredOps {

        @Override
        public void onUpdateCertificate(String certificate) {
        }

        @Override
        public void onError(String errorMsg) {
        }
    }

    @Test
    public void LogMessageTest(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        DbWorker dbWrkInst = new DbWorker(appContext);
        Assert.assertTrue("tst1 set failure",dbWrkInst.setEventLog(new EventLogDataContract(-1,"tst1 msg","","Error")));
        Assert.assertTrue("tst2 set failure",dbWrkInst.setEventLog(new EventLogDataContract(-1,"tst2 msg","","Error")));
        Assert.assertTrue("tst3 set failure",dbWrkInst.setEventLog(new EventLogDataContract(-1,"tst3 msg","","Error")));
        EventLogDataContract[] result= dbWrkInst.getEventLog("","",-1,"");
        Assert.assertTrue("no result",result!=null&&result.length>0);
    }
}
