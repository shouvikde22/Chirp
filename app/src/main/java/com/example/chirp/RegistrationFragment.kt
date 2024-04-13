package com.example.chirp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chirp.databinding.FragmentRegistrationBinding
import com.example.fashionadvisor.com.example.chirp.CheckNetwork
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class RegistrationFragment : Fragment() {

    private lateinit var  binding : FragmentRegistrationBinding
    private lateinit var deRef : DatabaseReference

    private lateinit var  firebaseAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()
        deRef = FirebaseDatabase.getInstance().getReference().child("User")

        var user : User

        binding.btnSignUp.setOnClickListener{
            if(CheckNetwork.isInternetAvailable(requireActivity())){
                user = User(
                    email = binding.editTextEmail.text.toString(),
                    name = binding.editTextName.text.toString(),
                    password = binding.editTextPass.text.toString(),
                    picture = null,
                    bio = null
                )
                if(validateField(user)) registerUser(user)
            }else{
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun validateField(user: User): Boolean {
        if(user.name?.isNotEmpty() == true && user.email!!.isNotEmpty() && user.password!!.isNotEmpty()) {
            user.name!!.forEach{ char ->
                if (char.isDigit()){
                    Log.d("name", " has digit $char")
                    binding.editTextName.error = "Invalid Username"
                    return false
                }
            }

            if(!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(),user.email)){
                binding.editTextEmail.error = "Invalid Email Address"
                return false
            }

            if(user.password.length <= 6){
                binding.editTextPass.error = "password is too short"
                return false
            }

        }
        else{
            Toast.makeText(context, "Empty Fields are not allowed" , Toast.LENGTH_SHORT).show()
            return false

        }

        return true
    }

    private fun registerUser(user: User){
        firebaseAuth.createUserWithEmailAndPassword(user.email.toString(), user.password.toString())
            .addOnSuccessListener {
                Toast.makeText(context, "Register Successfully", Toast.LENGTH_SHORT).show()
                binding.editTextName.setText("")
                binding.editTextEmail.setText("")
                binding.editTextPass.setText("")

                deRef.child(firebaseAuth.currentUser?.uid.toString()).setValue((user))
                    .addOnSuccessListener {
                        Toast.makeText(context, "Firestore Successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(context, "firestore Failed", Toast.LENGTH_SHORT).show()
                    }
                StoreUser.saveData(user, requireActivity())
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }.addOnFailureListener {
                Toast.makeText(context, "Register Failed", Toast.LENGTH_SHORT).show()
            }
    }
}