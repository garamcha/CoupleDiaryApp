package com.example.couplediary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.couplediary.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding // 회원가입 viewBinding
    private lateinit var auth : FirebaseAuth //파이어 베이스 인증 처리

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding = ActivityRegisterBinding.inflate(layoutInflater) // 레이아웃 객체를 생성하고 바인딩
        setContentView(binding.root)

        // 상단 액션바 뒤로가기 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 회원가입 버튼을 눌렀을 때 계정 생성
        binding.btnRegister.setOnClickListener {
            // 회원가입 처리 시작
            var strEmail = binding.etEmail.text.toString().trim() // 회원가입 이메일 아이디를 가져옴 (공백제거)
            var strPwd = binding.etPwd.text.toString().trim() // 회원가입 비밀번호를 가져옴 (공백제거)
            createAccount(strEmail, strPwd) // 계정 생성하기
        }//
    } // fun onCreate

    // 계정 생성 함수
    private fun createAccount(email: String, pwd : String){
        // Firebase Auth 진행
        if(email.isNotEmpty() && pwd.isNotEmpty()){ // email과 password가 공백이 아닐 경우
            auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener{
                if(it.isSuccessful){ // Email과 Pwd가 일치 했을 경우
                    // 회원가입에 성공한 경우
                    Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show()
                }
                else{
                    // 회원 가입에 실패한 경우
                    Toast.makeText(this, "이미 존재하는 계정이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }//private fun createAccount

    // 상단바 '<-'를 클릭 했을 경우
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }// onOptionsItemSelected()
}