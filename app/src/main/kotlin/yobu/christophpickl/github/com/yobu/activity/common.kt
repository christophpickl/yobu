package yobu.christophpickl.github.com.yobu.activity

import android.support.v7.app.AppCompatActivity
import yobu.christophpickl.github.com.yobu.R


fun AppCompatActivity.displayYobuLogo() {
    supportActionBar!!.setDisplayShowHomeEnabled(true)
    supportActionBar!!.setLogo(R.drawable.yobu_actionbar_logo)
    supportActionBar!!.setDisplayUseLogoEnabled(true)
}
