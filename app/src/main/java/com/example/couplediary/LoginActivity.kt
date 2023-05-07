package com.example.couplediary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.couplediary.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth //파이어 베이스 인증 처리

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        // 회원가입 버튼 클릭시
        binding.btnRegister.setOnClickListener {
            //회원가입 화면으로 이동
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼 클릭 시
        binding.btnLogin.setOnClickListener {
            // 로그인
            signIn(binding.etEmail.text.toString().trim(), binding.etPwd.text.toString().trim())
            // 로그인 창 email 정보                          , 비밀번호 정보
        }
    } // onCreate()

    // 로그아웃하지 않을 시 자동 로그인, 회원가입 시 바로 로그인 됨
    override fun onStart() {
        super.onStart()
        moveMainPage(auth.currentUser)
    }

    //로그인
    private fun signIn(email: String, password: String){
        if(email.isNotEmpty() && password.isNotEmpty()){ // email과 password가 공백이 아닐 경우
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                    moveMainPage(auth.currentUser)
                }
                else{
                    Toast.makeText(this, "아이디 혹은 비밀번호 입력 오류", Toast.LENGTH_LONG).show()
                }
            }

        }
    } // signIn()

    // 메인페이지로 이동
    private fun moveMainPage(user: FirebaseUser?) {
        if(user!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    } // moveMainPage()
}