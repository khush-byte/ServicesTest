package com.example.servicestest

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

class MyService : Service() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    //Create service
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    //Run services functions. This part use main flow
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        val start = intent?.getIntExtra(EXTRA_START, 0) ?: 0
        coroutineScope.launch {
            for (i in start until start + 100) {
                delay(1000)
                log("Timer: $i")
            }
        }
        //return super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    //Kill service
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun log(msg: String) {
        Log.d("MyServices", "MyService: $msg")
    }

    //Fabric method
    companion object {
        private const val EXTRA_START = "start"
        fun newIntent(context: Context, start: Int): Intent {
            return Intent(context, MyService::class.java).apply {
                putExtra(EXTRA_START, start)
            }
        }
    }
}