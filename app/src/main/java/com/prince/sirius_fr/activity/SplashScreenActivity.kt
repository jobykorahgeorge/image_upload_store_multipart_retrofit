package com.prince.sirius_fr.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.prince.sirius_fr.R

class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            startActivity(Intent(this@SplashScreenActivity, SelectionActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish()
        }, SPLASH_DELAY)

    }
}