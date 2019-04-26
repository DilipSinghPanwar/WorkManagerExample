package com.workmanagerexample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import static com.workmanagerexample.CustomWorkManager.EXTRA_OUTPUT_MESSAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName() + ">>";
    /**
     * One Time Work
     */
    private Button mBtnOneTimeWork;
    /**
     * One Time Work Cancel
     */
    private Button mBtnOneTimeWorkCancel;
    /**
     * Periodic Work
     */
    private Button mBtnPeriodicWork;
    /**
     * Periodic Work Cancel
     */
    private Button mBtnPeriodicWorkCancell;
    private Data data;
    private Constraints constraints;
    private OneTimeWorkRequest oneTimeWorkRequest;
    private PeriodicWorkRequest periodicWorkRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        The default time for PeriodicWorkRequest is 15 minutes. So below 15 minutes, it will not work properly.
    }

    private void initView() {
        mBtnOneTimeWork = findViewById(R.id.btnOneTimeWork);
        mBtnOneTimeWork.setOnClickListener(this);
        mBtnOneTimeWorkCancel = findViewById(R.id.btnOneTimeWorkCancel);
        mBtnOneTimeWorkCancel.setOnClickListener(this);
        mBtnPeriodicWork = findViewById(R.id.btnPeriodicWork);
        mBtnPeriodicWork.setOnClickListener(this);
        /*mBtnPeriodicWorkCancell = findViewById(R.id.btnPeriodicWorkCancel);
        mBtnPeriodicWorkCancell.setOnClickListener(this);*/

        data = new Data.Builder()
                .putString(CustomWorkManager.EXTRA_TITLE, "Message from Activity!")
                .putString(CustomWorkManager.EXTRA_TEXT, "Hi! I have come from activity.")
                .build();

        constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(true)
                .setRequiresDeviceIdle(true)
                .build();

        oneTimeWorkRequest = new OneTimeWorkRequest.Builder(CustomWorkManager.class)
                .setInputData(data)
                .setConstraints(constraints)
//                .setInitialDelay(15, TimeUnit.MINUTES)
                .addTag("simple_work")
                .build();

        periodicWorkRequest = new PeriodicWorkRequest.Builder(CustomWorkManager.class, 15, TimeUnit.MINUTES)
                .setInputData(data)
                .setConstraints(constraints)
//                .setInitialDelay(15, TimeUnit.MINUTES)
                .addTag("periodic_work")
                .build();

        try {
            //Listening to the work status
            WorkManager.getInstance().getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(@Nullable WorkInfo workInfo) {
                            //Displaying the status into TextView
                            Log.e(TAG, "getState: " + workInfo.getState().name() + "\n");
                            if (workInfo.getState().isFinished()) {
                                String myResult = workInfo.getOutputData().getString(EXTRA_OUTPUT_MESSAGE);
                                Log.e(TAG, "myResult: " + myResult);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btnOneTimeWork:
                WorkManager.getInstance().enqueue(oneTimeWorkRequest);
                break;
            case R.id.btnOneTimeWorkCancel:
                WorkManager.getInstance().cancelAllWorkByTag("simple_work");
                //WorkManager.getInstance().cancelWorkById(oneTimeWorkRequestID);
                break;
            case R.id.btnPeriodicWork:
                WorkManager.getInstance().enqueue(periodicWorkRequest);
                break;
            /*case R.id.btnPeriodicWorkCancel:
                WorkManager.getInstance().cancelAllWorkByTag("periodic_work");
//                WorkManager.getInstance().cancelWorkById(periodicWorkRequestID);
                break;*/
        }
    }
}