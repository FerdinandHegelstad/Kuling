package com.example.team22.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.example.team22.R
import com.example.team22.view_models.MainActivityViewModel


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // remove top statusbar for better presentation:
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        val viewModel =
            ViewModelProvider(this).get(MainActivityViewModel::class.java)
        //show splashscreen for 1 sec:
        Handler().postDelayed({

            //redirect to appropriate view according to if app is running for first time or not:
            if (viewModel.firstRun()) {
                startActivity(Intent(this@SplashActivity, UserPreferencesActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, NavigationActivity::class.java))
            }
            //destroy this activity:
            finish()
        }, 1000)
    }
}
