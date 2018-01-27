package com.dimidych.policyservicestarter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.dimidych.policydbworker.DbWorker;
import com.dimidych.policydbworker.EventLogDataContract;
import com.dimidych.policydbworker.PolicySetAsyncTaskLoader;
import com.dimidych.policydbworker.PolicySetDataContract;
import com.dimidych.policydbworker.Result;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PolicyService extends Service {
    private ExecutorService _executor;
    private DbWorker _model;

    public PolicyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _model = new DbWorker(this);
        _executor = Executors.newFixedThreadPool(4);
        _model.onSetLog("PolicyService created", "Уведомление", -1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            PolicySetAsyncTask policySetAsyncTask = new PolicySetAsyncTask(startId, this);
            _executor.execute(policySetAsyncTask);
            _model.onSetLog("PolicyService starts execution", "Уведомление", -1);
        } catch (Exception ex) {
            _model.setEventLog(new EventLogDataContract(-1, "Ошибка запуска службы. " + ex, "", "Error"));
        }

        return START_STICKY;//super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        _model.onSetLog("PolicyService destroyed", "Уведомление", -1);
    }

    class PolicySetAsyncTask implements Runnable {
        private int _startId;
        private Context _ctx;

        public PolicySetAsyncTask(int startId, Context ctx) {
            _startId = startId;
            _ctx = ctx;
        }

        @Override
        public void run() {
            try {
                PolicySetAsyncTaskLoader policySetAtLoader = new PolicySetAsyncTaskLoader(_ctx);

                while (true) {
                    Result<ArrayList<Map.Entry<PolicySetDataContract, String>>> result = policySetAtLoader.loadInBackground();

                    if (result != null && !result.BoolRes)
                        _model.onSetLog("PolicyService iteration finished with error " + result.ErrorRes, "Error", -1);

                    Thread.sleep(60000);
                }
            } catch (Exception ex) {
                _model.onSetLog("PolicyService execution error " + ex.toString(), "Error", -1);
                stopSelfResult(_startId);
            }
        }
    }
}
