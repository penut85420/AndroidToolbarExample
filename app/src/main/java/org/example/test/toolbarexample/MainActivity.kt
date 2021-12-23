package org.example.test.toolbarexample

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import kotlin.system.exitProcess
import android.view.Menu as ViewMenu

class MainActivity : AppCompatActivity() {

    private var mMenu: ViewMenu? = null
    private var isDarkMode: Boolean = false
    private var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        pref = getPreferences(Context.MODE_PRIVATE)
        isDarkMode = pref!!.getBoolean("Hello", false)
        if (isDarkMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        Log.d(LOG_TAG, "onCreate isMenuChecked: $isDarkMode")
        Thread.setDefaultUncaughtExceptionHandler(ErrorHandler())
    }

    class ErrorHandler : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(pThread: Thread, pException: Throwable) {
            Log.e(LOG_TAG, "OMG!!")
            for (st in pThread.stackTrace) {
                Log.e(LOG_TAG, "T: $st")
            }
            val errMsg = Log.getStackTraceString(pException)
            Log.e(LOG_TAG, errMsg)
            exitProcess(2)
        }
    }

    override fun onPause() {
        with(pref!!.edit()) {
            putBoolean("Hello", mMenu!![2].isChecked)
            apply()
        }
        Log.d(LOG_TAG, "onPause isMenuChecked: ${pref!!.getBoolean("Hello", false)}")
        super.onPause()
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy isMenuChecked: ${pref!!.getBoolean("Hello", false)}")
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: ViewMenu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        menu!!.getItem(2).isChecked = isDarkMode
        mMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.itemHello -> {

                toast(hello("World!"))
                true
            }
            R.id.itemHelp -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                toast("Help!")
                true
            }
            R.id.itemDark -> {
                item.isChecked = !item.isChecked
                isDarkMode = item.isChecked
                if (isDarkMode)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                true
            }
            R.id.itemExplosion -> {
                Thread { raiseError() }.start()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                Log.d("Hello", "UI_MODE_NIGHT_NO")
            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                Log.d("Hello", "UI_MODE_NIGHT_YES")
            } // Night mode is active, we're using dark theme
        }
    }

    override fun onPrepareOptionsMenu(menu: ViewMenu): Boolean {
        return true
    }

    private fun toast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    private fun raiseError() {
        throw Exception()
    }

    companion object {
        private const val LOG_TAG = "Hello"

        init {
            System.loadLibrary("toolbarexample")
        }
    }

    private external fun hello(s: String): String
}
