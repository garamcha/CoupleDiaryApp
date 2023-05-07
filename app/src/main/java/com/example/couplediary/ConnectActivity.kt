package com.example.couplediary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.couplediary.databinding.ActivityDdayBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ConnectActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var binding : ActivityDdayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        // 상단 액션바 뒤로가기 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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