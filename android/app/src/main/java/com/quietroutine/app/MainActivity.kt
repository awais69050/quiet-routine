package com.quietroutine.app

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.quietroutine.app.core.di.ServiceLocator
import com.quietroutine.app.core.navigation.Navigator
import com.quietroutine.app.databinding.ActivityMainBinding
import com.quietroutine.app.presentation.splash.SplashFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val graph = ServiceLocator.init(applicationContext)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (graph.settings.keepAwake) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        if (savedInstanceState == null) {
            Navigator.showRoot(
                supportFragmentManager,
                SplashFragment(),
                Navigator.TAG_SPLASH
            )
        }
    }
}
