package com.example.couplediary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.couplediary.databinding.ActivityDdayBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DDayActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth //파이어 베이스 인증 처리
    private lateinit var database : FirebaseDatabase
    private lateinit var binding : ActivityDdayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding = ActivityDdayBinding.inflate(layoutInflater)  // 레이아웃 객체를 생성하고 바인딩
        setContentView(binding.root)

        // 상단 액션바 뒤로가기 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 파이어베이스 인스턴스 생성
        database = FirebaseDatabase.getInstance()
        val myRef : DatabaseReference = database.getReference(auth.currentUser!!.uid)

        // Dday 입력 버튼을 눌렀을 때
        binding.ddayBtn.setOnClickListener {
            val date = binding.dDayEtText.text.toString().trim()

            Log.d("date", date)

            // 데이터 베이스에 Dday 날짜 저장하기
            myRef.child("Dday").setValue(date)
            Toast.makeText(this, "데이터베이스 저장 성공", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

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