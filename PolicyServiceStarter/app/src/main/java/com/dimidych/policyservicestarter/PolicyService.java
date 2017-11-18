package com.dimidych.policyservicestarter;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.dimidych.policydbworker.PolicySetAsyncTaskLoader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PolicyService extends Service {
    private ExecutorService _executor;

    @Override
    public void onCreate() {
        super.onCreate();
        _executor = Executors.newFixedThreadPool(8);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        PolicySetAsyncTask policySetAsyncTask = new PolicySetAsyncTask(startId);
        _executor.execute(policySetAsyncTask);
        return START_STICKY;//super.onStartCommand(intent, flags, startId);
    }

    class PolicySetAsyncTask implements Runnable {

        private int _startId;

        public PolicySetAsyncTask(int startId) {
            _startId = startId;
        }

        @Override
        public void run() {
            try {
                Context ctx = getApplicationContext();
                PolicySetAsyncTaskLoader policySetAtLoader = new PolicySetAsyncTaskLoader(ctx);

                while (true) {
                    policySetAtLoader.loadInBackground();
                    Thread.sleep(60000);
                }
            } catch (Exception e) {
                stopSelfResult(_startId);
                e.printStackTrace();
            }
        }
    }
}
