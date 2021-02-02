package com.example.doordashproject

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doordashproject.databinding.MainActivityBinding
import com.google.android.material.snackbar.Snackbar
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {

    private var binding: MainActivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater).apply {
            ExceptionHandler.displayToast = {
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show() }

            ExceptionHandler.displayDialog = {
                AlertDialogFragment(it).show(supportFragmentManager, "alert") }

            ExceptionHandler.displaySnackbar = {
                Snackbar.make(this.root, it, Snackbar.LENGTH_SHORT).show() }

            setContentView(this.root)
        }
    }

    override fun onBackPressed() {
        EventBus.getDefault().post(BackEvent { super.onBackPressed() })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    class BackEvent(private val resumeBack: () -> Unit) {
        fun ignore() { resumeBack() }
    }
}