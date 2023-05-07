package com.example.couplediary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import com.example.couplediary.databinding.ActivityCoupleDiaryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CoupleDiaryActivity : AppCompatActivity() {
    // 현재 날짜 : Date -> String
    lateinit var binding : ActivityCoupleDiaryBinding

    var my_text : String = "아직 상대방이 일기를 작성하지 않았습니다."
    var your_text : String = "아직 상대방이 일기를 작성하지 않았습니다."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoupleDiaryBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_couple_diary)

        val myView : TextView = findViewById(R.id.my_Text)
        val yourView : TextView = findViewById(R.id.you_text)

        // 상단 액션바 뒤로가기 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(intent.getStringExtra("m_diary") == null){
            myView.setText(my_text)
        }else
            myView.setText(intent.getStringExtra("m_diary"))

        if(intent.getStringExtra("y_diary") == null){
            yourView.setText(your_text)
        }else
            yourView.setText(intent.getStringExtra("y_diary"))
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