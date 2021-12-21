package org.example.test.toolbarexample

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import kotlin.system.exitProcess
import android.view.Menu as ViewMenu

class MainActivity : AppCompatActivity() {

    private var mMenu: ViewMenu? = null
    private var isMenuChecked: Boolean = false
    private var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = getPreferences(Context.MODE_PRIVATE)
        isMenuChecked = pref!!.getBoolean("Hello", false)
        Log.d(LOG_TAG, "onCreate isMenuChecked: $isMenuChecked")

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
        menu!!.getItem(2).isChecked = isMenuChecked
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
                toast("Help!")
                true
            }
            R.id.itemEnable -> {
                item.isChecked = !item.isChecked
                isMenuChecked = true
                true
            }
            R.id.itemExplosion -> {
                Thread { raiseError() }.start()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
