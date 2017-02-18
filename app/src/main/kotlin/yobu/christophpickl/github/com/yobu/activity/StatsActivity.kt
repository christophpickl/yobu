package yobu.christophpickl.github.com.yobu.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import org.jetbrains.anko.*


class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatsActivityUi().setContentView(this)
    }

    fun onFinish() {
        finish()
    }

}

// example: https://github.com/yanex/anko-example/blob/master/app/src/main/java/org/example/ankodemo/MainActivity.kt
class StatsActivityUi : AnkoComponent<StatsActivity> {

    @SuppressLint("SetTextI18n")
    override fun createView(ui: AnkoContext<StatsActivity>) = with(ui) {
        verticalLayout {
            padding = ui.dip(20)

            textView {
                //                height
                gravity = Gravity.FILL
                textSize = 18f
                text = """some foo: 12
some bar: 42"""
            }

            button("Zurück") {
                // MINOR could display the back button in top panel where the context menu is in main activity
                onClick { ui.owner.onFinish() }
            }
        }
    }

}