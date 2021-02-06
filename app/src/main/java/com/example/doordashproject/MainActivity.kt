package com.example.doordashproject

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doordashproject.databinding.MainActivityBinding
import com.google.android.material.snackbar.Snackbar
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {

    private var _binding: MainActivityBinding? = null
    //private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = MainActivityBinding.inflate(layoutInflater).apply {
            ExceptionHandler.toast = {
                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show(); 0}

            ExceptionHandler.dialog = {
                AlertDialogFragment(it ?: "ERROR").show(supportFragmentManager, "alert"); 0 }

            ExceptionHandler.snackbar = {
                Snackbar.make(root, it ?: "ERROR", Snackbar.LENGTH_SHORT).show(); 0 }

            setContentView(root)
        }
    }

    override fun onBackPressed() {
        EventBus.getDefault().post(BackEvent { super.onBackPressed() })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    class BackEvent(private val resumeBack: () -> Unit) {
        fun ignore() { resumeBack() }
    }
}