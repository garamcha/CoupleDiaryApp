package com.example.couplediary

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.couplediary.databinding.ActivityMyDiaryAddBinding
import com.example.couplediary.ui.home.Diary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyDiaryEditActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyDiaryAddBinding

    // ListFragment에서 넘겨받은 값
    var position : Int = 0
    lateinit var dayDataList : ArrayList<String>

    lateinit var auth : FirebaseAuth
    val database = Firebase.database

    // db에 날짜로 데이터 넣고 가져오기
    val long_now = System.currentTimeMillis()
    val t_date = Date(long_now)
    val t_dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
    val str_date = t_dateFormat.format(t_date)

    var weather : Int = 0
    var emotions : Int = 0

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyDiaryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get Data from ListFragment
        position = intent.getIntExtra("position", 0)
        dayDataList = intent.getStringArrayListExtra("dayDataList") as ArrayList<String>

        auth = FirebaseAuth.getInstance()

        val dateView : TextView = binding.dirayAddDate
        dateView.text = str_date
        // 상단 액션바 뒤로가기 버튼 추가
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val weather_sun : ImageView = findViewById(R.id.weather_sun)
        val weather_cloud : ImageView = findViewById(R.id.weather_cloud)
        val weather_rain : ImageView = findViewById(R.id.weather_rain)
        val weather_snow : ImageView = findViewById(R.id.weather_snow)
        val emotions_grinning : ImageView = findViewById(R.id.emotions_grinning)
        val emotions_neutral : ImageView = findViewById(R.id.emotions_neutral)
        val emotions_sadness : ImageView = findViewById(R.id.emotions_sadness)
        val emotions_disturb : ImageView = findViewById(R.id.emotions_disturb)
        val emotions_crying : ImageView = findViewById(R.id.emotions_crying)
        val emotions_angry : ImageView = findViewById(R.id.emotions_angry)

        // 날씨 해 클릭했을 때
        weather_sun.setOnClickListener {
            weather = 1
            // 클릭한 날씨를 제외한 나머지 이미지 투명도 30% 설정
            weather_sun.alpha = 1f
            weather_cloud.alpha = 0.3f
            weather_rain.alpha = 0.3f
            weather_snow.alpha = 0.3f
        }
        // 날씨 흐림 클릭했을 때
        weather_cloud.setOnClickListener {
            weather = 2
            // 클릭한 날씨를 제외한 나머지 이미지 투명도 30% 설정
            weather_sun.alpha = 0.3f
            weather_cloud.alpha = 1f
            weather_rain.alpha = 0.3f
            weather_snow.alpha = 0.3f
        }
        // 날씨 비 클릭했을 때
        weather_rain.setOnClickListener {
            weather = 3
            // 클릭한 날씨를 제외한 나머지 이미지 투명도 30% 설정
            weather_sun.alpha = 0.3f
            weather_cloud.alpha = 0.3f
            weather_rain.alpha = 1f
            weather_snow.alpha = 0.3f
        }
        // 날씨 눈 클릭했을 때
        weather_snow.setOnClickListener {
            weather = 4
            // 클릭한 날씨를 제외한 나머지 이미지 투명도 30% 설정
            weather_sun.alpha = 0.3f
            weather_cloud.alpha = 0.3f
            weather_rain.alpha = 0.3f
            weather_snow.alpha = 1f
        }
        /*=====================================================================*/
        // 감정 웃음 클릭했을 때
        emotions_grinning.setOnClickListener {
            emotions = 1
            // 클릭한 감정을 제외한 나머지 이미지 투명도 30% 설정
            emotions_grinning.alpha = 1f
            emotions_neutral.alpha = 0.3f
            emotions_sadness.alpha = 0.3f
            emotions_disturb.alpha = 0.3f
            emotions_crying.alpha = 0.3f
            emotions_angry.alpha = 0.3f
        }
        // 감정 무표정 클릭했을 때
        emotions_neutral.setOnClickListener {
            emotions = 2
            // 클릭한 감정을 제외한 나머지 이미지 투명도 30% 설정
            emotions_grinning.alpha = 0.3f
            emotions_neutral.alpha = 1f
            emotions_sadness.alpha = 0.3f
            emotions_disturb.alpha = 0.3f
            emotions_crying.alpha = 0.3f
            emotions_angry.alpha = 0.3f
        }
        // 감정 슬픔 클릭했을 때
        emotions_sadness.setOnClickListener {
            emotions = 3
            // 클릭한 감정을 제외한 나머지 이미지 투명도 30% 설정
            emotions_grinning.alpha = 0.3f
            emotions_neutral.alpha = 0.3f
            emotions_sadness.alpha = 1f
            emotions_disturb.alpha = 0.3f
            emotions_crying.alpha = 0.3f
            emotions_angry.alpha = 0.3f
        }
        // 감정 혼란 클릭했을 때
        emotions_disturb.setOnClickListener {
            emotions = 4
            // 클릭한 감정을 제외한 나머지 이미지 투명도 30% 설정
            emotions_grinning.alpha = 0.3f
            emotions_neutral.alpha = 0.3f
            emotions_sadness.alpha = 0.3f
            emotions_disturb.alpha = 1f
            emotions_crying.alpha = 0.3f
            emotions_angry.alpha = 0.3f
        }
        // 감정 눈물 클릭했을 때
        emotions_crying.setOnClickListener {
            emotions = 5
            // 클릭한 감정을 제외한 나머지 이미지 투명도 30% 설정
            emotions_grinning.alpha = 0.3f
            emotions_neutral.alpha = 0.3f
            emotions_sadness.alpha = 0.3f
            emotions_disturb.alpha = 0.3f
            emotions_crying.alpha = 1f
            emotions_angry.alpha = 0.3f
        }
        // 감정 화남 클릭했을 때
        emotions_angry.setOnClickListener {
            emotions = 6
            // 클릭한 감정을 제외한 나머지 이미지 투명도 30% 설정
            emotions_grinning.alpha = 0.3f
            emotions_neutral.alpha = 0.3f
            emotions_sadness.alpha = 0.3f
            emotions_disturb.alpha = 0.3f
            emotions_crying.alpha = 0.3f
            emotions_angry.alpha = 1f
        }


        val editText : EditText = findViewById(R.id.editTextTextPersonName)

        // DB에서 data 받아와 뿌려주기
        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    dateView.text = snapshot.child("day").child(dayDataList[position]).key

                    // DB 값으로 weather 설정
                    if((snapshot.child("day").child(dayDataList[position]).child("weather").getValue()) == 1L){
                        weather = 1
                        weather_sun.alpha = 1f
                        weather_cloud.alpha = 0.3f
                        weather_rain.alpha = 0.3f
                        weather_snow.alpha = 0.3f
                    }
                    if(snapshot.child("day").child(dayDataList[position]).child("weather").getValue() == 2L){
                        weather = 2
                        weather_sun.alpha = 0.3f
                        weather_cloud.alpha = 1f
                        weather_rain.alpha = 0.3f
                        weather_snow.alpha = 0.3f
                    }
                    if(snapshot.child("day").child(dayDataList[position]).child("weather").getValue() == 3L){
                        weather = 3
                        weather_sun.alpha = 0.3f
                        weather_cloud.alpha = 0.3f
                        weather_rain.alpha = 1f
                        weather_snow.alpha = 0.3f
                    }
                    if(snapshot.child("day").child(dayDataList[position]).child("weather").getValue() == 4L){
                        weather = 4
                        weather_sun.alpha = 0.3f
                        weather_cloud.alpha = 0.3f
                        weather_rain.alpha = 0.3f
                        weather_snow.alpha = 1f
                    }

                    // DB 값으로 emotions 설정
                    if((snapshot.child("day").child(dayDataList[position]).child("emotions").getValue()) == 1L){
                        emotions = 1
                        emotions_grinning.alpha = 1f
                        emotions_neutral.alpha = 0.3f
                        emotions_sadness.alpha = 0.3f
                        emotions_disturb.alpha = 0.3f
                        emotions_crying.alpha = 0.3f
                        emotions_angry.alpha = 0.3f
                    }
                    if((snapshot.child("day").child(dayDataList[position]).child("emotions").getValue()) == 2L){
                        emotions = 2
                        emotions_grinning.alpha = 0.3f
                        emotions_neutral.alpha = 1f
                        emotions_sadness.alpha = 0.3f
                        emotions_disturb.alpha = 0.3f
                        emotions_crying.alpha = 0.3f
                        emotions_angry.alpha = 0.3f
                    }
                    if((snapshot.child("day").child(dayDataList[position]).child("emotions").getValue()) == 3L){
                        emotions = 3
                        emotions_grinning.alpha = 0.3f
                        emotions_neutral.alpha = 0.3f
                        emotions_sadness.alpha = 1f
                        emotions_disturb.alpha = 0.3f
                        emotions_crying.alpha = 0.3f
                        emotions_angry.alpha = 0.3f
                    }
                    if((snapshot.child("day").child(dayDataList[position]).child("emotions").getValue()) == 4L){
                        emotions = 4
                        emotions_grinning.alpha = 0.3f
                        emotions_neutral.alpha = 0.3f
                        emotions_sadness.alpha = 0.3f
                        emotions_disturb.alpha = 1f
                        emotions_crying.alpha = 0.3f
                        emotions_angry.alpha = 0.3f
                    }
                    if((snapshot.child("day").child(dayDataList[position]).child("emotions").getValue()) == 5L){
                        emotions = 5
                        emotions_grinning.alpha = 0.3f
                        emotions_neutral.alpha = 0.3f
                        emotions_sadness.alpha = 0.3f
                        emotions_disturb.alpha = 0.3f
                        emotions_crying.alpha = 1f
                        emotions_angry.alpha = 0.3f
                    }
                    if((snapshot.child("day").child(dayDataList[position]).child("emotions").getValue()) == 6L){
                        emotions = 6
                        emotions_grinning.alpha = 0.3f
                        emotions_neutral.alpha = 0.3f
                        emotions_sadness.alpha = 0.3f
                        emotions_disturb.alpha = 0.3f
                        emotions_crying.alpha = 0.3f
                        emotions_angry.alpha = 1f
                    }

                    editText.setText(snapshot.child("day").child(dayDataList[position]).child("diary").getValue() as String)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        // 플로팅 버튼 클릭 시
        binding!!.floatingActionButton.setOnClickListener {
            if(emotions == 0 || weather == 0){
                Toast.makeText(this, "감정 또는 날씨가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                val myRef = database.getReference(auth.currentUser!!.uid)
                myRef.child("day").child(str_date).setValue(Diary(str_date, weather, emotions, editText.text.toString()))
                finish()
            }
        }

    }

    // 상단바 '<-'를 클릭 했을 경우
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }else -> {
            return super.onOptionsItemSelected(item)
        }
        }
    }// onOptionsItemSelected()
}