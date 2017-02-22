package yobu.christophpickl.github.com.yobu

import android.app.Application
import yobu.christophpickl.github.com.yobu.activity.MainActivity
import yobu.christophpickl.github.com.yobu.common.LOG


//class MyApp : Application(), KodeinAware {
//    override val kodein by Kodein.lazy {
//        /* bindings */
//    }
//}

class YobuApplication : Application() {
    companion object {
        private val LOG = LOG(MainActivity::class.java)
    }

    override fun onCreate() {
        LOG.i("onCreate()")
        super.onCreate()
    }

    override fun onTerminate() {
        LOG.i("onTerminate()")
        super.onTerminate()
    }
}
