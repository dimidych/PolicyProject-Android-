package com.dimidych.policydbworker;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
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
        DevicePolicyAdmin.requestAdminService(new MockActivity(), new ComponentName(appContext,
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

    class MockActivity extends AppCompatActivity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }
}
