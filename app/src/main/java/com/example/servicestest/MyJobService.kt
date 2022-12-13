package com.example.servicestest

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.os.PersistableBundle
import android.util.Log
import kotlinx.coroutines.*

class MyJobService : JobService() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartCommand")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            coroutineScope.launch {
                var workItem = params?.dequeueWork()
                while (workItem != null) {
                    //return 0 if null
                    //val page = params?.extras?.getInt(PAGE) ?: 0
                    val page = workItem.intent.getIntExtra(PAGE, 0)
                    for (i in 0 until 5) {
                        delay(1000)
                        log("Timer: $i $page")
                    }
                    params?.completeWork(workItem)
                    workItem = params?.dequeueWork()
                }
                jobFinished(params, false)//finish service totally
            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        log("onStopJob")
        //planning restart of service
        return true
    }

    private fun log(msg: String) {
        Log.d("MyServices", "MyJobService: $msg")
    }

    //Kill service
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    companion object {
        const val JOB_ID = 111
        private const val PAGE = "page"

//        fun newBundle(page: Int): PersistableBundle{
//            return PersistableBundle().apply {
//                putInt(PAGE, page)
//            }
//        }

        fun newIntent(page: Int): Intent{
            return Intent().apply {
                putExtra(PAGE, page)
            }
        }
    }
}