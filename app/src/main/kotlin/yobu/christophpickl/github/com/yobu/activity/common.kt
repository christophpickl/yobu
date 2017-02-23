package yobu.christophpickl.github.com.yobu.activity


fun AppCompatActivity.displayYobuLogo() {
    supportActionBar!!.setDisplayShowHomeEnabled(true)
    supportActionBar!!.setLogo(R.drawable.yobu_actionbar_logo)
    supportActionBar!!.setDisplayUseLogoEnabled(true)
}
