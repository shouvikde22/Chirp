package com.example.chirp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chirp.databinding.FragmentLoginBinding
import com.example.fashionadvisor.CheckNetwork
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef : DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var user : User
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        firebaseAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference().child("User")
        binding.btnSignIn.setOnClickListener{
            if(CheckNetwork.isInternetAvailable(requireActivity())){
                user = User(
                    email = binding.editTextEmail.text.toString().trim(),
                    name = null,
                    password = binding.editTextPass.text.toString().trim())
                loginUser(user)
            }else{
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun loginUser(user: User){
        firebaseAuth.signInWithEmailAndPassword(user.email.toString(), user.password.toString())
            .addOnSuccessListener {
                Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                binding.editTextEmail.setText("")
                binding.editTextPass.setText("")
                getUserDetails(user.email.toString())

            }.addOnFailureListener {
                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUserDetails(email : String?){
        dbRef.child(firebaseAuth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("fetched user", document.toString())
                    val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
                    val userModel = userViewModel.convertToUserModel(document)
                    StoreUser.saveData(userModel, requireActivity())
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                } else {
                    Log.d("fetched user", "falied")
                }
            }
    }

}