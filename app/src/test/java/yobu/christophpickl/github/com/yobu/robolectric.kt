package yobu.christophpickl.github.com.yobu

import android.support.v7.app.AppCompatActivity
import org.junit.runner.RunWith
import org.junit.runners.model.InitializationError
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(CustomRobolectricTestRunner::class)
abstract class RobolectricTest {

    fun withTestActivity(code: (AppCompatActivity) -> Unit) {
        val activity = Robolectric.setupActivity(TestActivity::class.java)
        activity.execute(code)
    }

}

class TestActivity : AppCompatActivity() {
    fun execute(code: (AppCompatActivity) -> Unit) {
        code(this)
    }
}

class CustomRobolectricTestRunner @Throws(InitializationError::class)
constructor(testClass: Class<*>) : RobolectricTestRunner(testClass) {

    override fun buildGlobalConfig(): Config {
        return Config.Builder.defaults()
                .setSdk(19)
                .setPackageName("yobu.christophpickl.github.com.yobu")
                .setManifest("src/main/AndroidManifest.xml")
                .setResourceDir("../../build/intermediates/res/merged/debug") // relative to manifest
                .setAssetDir("../../build/intermediates/assets/debug") // relative to manifest
                .build()
    }
}
