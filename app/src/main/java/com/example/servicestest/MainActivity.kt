package com.example.servicestest

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.servicestest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.simpleService.setOnClickListener {
            startService(MyService.newIntent(this, 25))
        }

        binding.foregroundService.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                MyForegroundService.newIntent(this)
            )
        }

        binding.intentService.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                MyIntentService.newIntent(this)
            )
        }

        binding.jobScheduler.setOnClickListener {
            val componentName = ComponentName(this, MyJobService::class.java)
            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
                .setRequiresCharging(true)//work only if charging
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) //work only if WIFI available
                .build()

            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = MyJobService.newIntent(page++)
                jobScheduler.enqueue(jobInfo, JobWorkItem(intent))
            }else{
                startService(MyIntentServiceForOldAPI.newIntent(this, page++))
            }
        }

        //For both new and old android version async schedule service
        binding.jobIntentService.setOnClickListener{
            MyJobIntentService.enqueue(this, page++)
        }

        binding.workManager.setOnClickListener {
            val workManager = WorkManager.getInstance(applicationContext)
            //set work in line, not duplicate task
            workManager.enqueueUniqueWork(
                MyWorker.WORK_NAME,
                //add work on line
                ExistingWorkPolicy.APPEND,
                //one time work request
                MyWorker.makeRequest(page++)
            )
        }
    }
}