package yobu.christophpickl.github.com.yobu

import android.app.Application
import com.github.salomonbrys.kodein.*
import yobu.christophpickl.github.com.yobu.common.Clock
import yobu.christophpickl.github.com.yobu.common.LOG
import yobu.christophpickl.github.com.yobu.common.RealClock
import yobu.christophpickl.github.com.yobu.logic.*
import yobu.christophpickl.github.com.yobu.logic.persistence.Preferences
import yobu.christophpickl.github.com.yobu.logic.persistence.QuestionStatisticsSqliteRepository
import yobu.christophpickl.github.com.yobu.logic.persistence.createPreferences

class YobuApplication : Application(), KodeinAware {

    companion object {
        private val log = LOG(YobuApplication::class.java)
    }

    override val kodein by Kodein.lazy {
        bind<RandX>() with instance(RandXImpl)
        bind<Clock>() with instance(RealClock)

        bind<Preferences>() with singleton { createPreferences() }
        bind<QuestionStatisticsRepository>() with singleton { CachedQuestionStatisticsRepository(QuestionStatisticsSqliteRepository(this@YobuApplication)) }
        bind<QuestionsGenerator>() with singleton { QuestionsGeneratorImpl(instance()) }
        bind<QuestionLoader>() with singleton { QuestionLoaderImpl(instance()) }

        bind<StatisticService>() with singleton { StatisticServiceImpl(instance(), instance()) }
        bind<QuestionSelector>() with singleton { QuestionSelectorImpl(instance(), instance()) }
    }

    override fun onCreate() {
        log.i("onCreate()")
        super.onCreate()
    }

    override fun onTerminate() {
        log.i("onTerminate()")
        super.onTerminate()

    }
}
