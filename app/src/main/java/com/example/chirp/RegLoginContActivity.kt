package com.example.chirp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.chirp.databinding.ActivityRegLoginContBinding

class   RegLoginContActivity : AppCompatActivity() {

var pageMode = LogReg.REGISTRATION
    private lateinit var binding : ActivityRegLoginContBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegLoginContBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        binding.textSwitchRegLog.text = getString(R.string.concat, getString(R.string.already_have_an_account), getString(R.string.sign_in))

        binding.textSwitchRegLog.setOnClickListener{
            if(pageMode == LogReg.REGISTRATION){
                binding.textSwitchRegLog.text = getString(R.string.concat, getString(R.string.don_t_have_an_account), getString(R.string.sign_up))
                changeFragment(LoginFragment())
                pageMode = LogReg.LOGIN
            }else{
                binding.textSwitchRegLog.text = getString(R.string.concat, getString(R.string.already_have_an_account), getString(R.string.sign_in))
                changeFragment(RegistrationFragment())
                pageMode = LogReg.REGISTRATION
            }
        }

    }

    private fun changeFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragContRegLog, fragment)
            .commit()
    }
}