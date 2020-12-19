package co.id.naufalnibros.myapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.id.naufalnibros.myapplication.databinding.ActivityMainBinding
import co.id.naufalnibros.myapplication.utils.viewBinding

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

}