package com.example.couplediary.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.couplediary.CoupleDiaryActivity
import com.example.couplediary.MainActivity
import com.example.couplediary.MyDiaryEditActivity
import com.example.couplediary.R
import com.example.couplediary.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.database.ktx.snapshots
import org.w3c.dom.Text
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment() {
    // Firebase Database
    private var _binding: FragmentHomeBinding? = null
    private lateinit var databaseRef : DatabaseReference
    lateinit var auth : FirebaseAuth

    // 현재 날짜 : Date -> String
    val now_time = System.currentTimeMillis()
    val d_now_time = Date(now_time)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
    // Date -> String
    val str_date: String = dateFormat.format(d_now_time)
    // MainActivity에서 가져온 Long 타입의 Date
    var longDate : Long = MainActivity().longDate

    private lateinit var calendarView : CalendarView
    private lateinit var dDayView : TextView

    var selectedDate : String = str_date

    // 실제 사용 데이터
    var weather : Int = 1
    var emotions : Int = 1
    var diary1_text : String = ""
    var diary2_text : String = "아직 상대방이 일기를 작성하지 않았습니다."

    // recyclerView
    private lateinit var adapter: DiaryAdapter
    private lateinit var recyclerView: RecyclerView
    lateinit var myDiaryArrayList: ArrayList<Diary>
    lateinit var yourDiaryArrayList: ArrayList<Diary>
    private lateinit var dayDataList : ArrayList<String>
    lateinit var diary_user1 : Array<String>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = FirebaseAuth.getInstance()

        databaseRef = FirebaseDatabase.getInstance().reference
        databaseRef.get().addOnSuccessListener {
        }
        return root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 최초 실행 여부를 판단 ->>
        val pref = requireActivity().getSharedPreferences("checkFirst", Activity.MODE_PRIVATE)
        val checkFirst = pref.getBoolean("checkFirst", false)

        if(!checkFirst){
            val editor : SharedPreferences.Editor = pref.edit()
            editor.putBoolean("checkFirst", true)
            editor.commit()

            myData()

            FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        // Dday 계산하기
                        if(snapshot.child("Dday").value != null){
                            val coupleDay = fewDay(snapshot.child("Dday").value.toString())
                            Log.d("HomeFragment", "coupleDay : " + coupleDay)
                            if(coupleDay != null)
                                //_binding!!.DdayCnt.text = "D+" + coupleDay.toString()
                                _binding!!.DdayCnt.text = "D+" + coupleDay.toString()
                            else if(coupleDay == null)
                                _binding!!.DdayCnt.text = "D+day"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }

        // 달력에서 선택한 데이터 받아오기
        calendarView = view.findViewById(R.id.calendarView)
        calendarView.setDate(longDate)
        calendarView.setOnDateChangeListener { view, year, tempMonth, tempDay ->
            var month = tempMonth + 1
            var day : String = tempDay.toString()
            if(tempDay < 10) day = "0$day"
            selectedDate = "$year-$month-$day"

            // selectedDate를 Long타입으로 변경
            longDate = (dateFormat.parse(selectedDate) as java.util.Date).time
            printData()
        }

        binding.myDiary.setOnClickListener {
            val intent = Intent(activity, CoupleDiaryActivity::class.java)

            intent.putExtra("m_diary", diary1_text)
            intent.putExtra("y_diary", diary2_text)
            startActivity(intent)
        }
        binding.youDiary.setOnClickListener {
            val intent = Intent(activity, CoupleDiaryActivity::class.java)

            intent.putExtra("m_diary", diary1_text)
            intent.putExtra("y_diary", diary2_text)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun printData(){
        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    if (snapshot.child("day").child(selectedDate).child("diary").getValue() != null) {
                        diary1_text = snapshot.child("day").child(selectedDate).child("diary").getValue() as String
                        _binding!!.myDiary.text = diary1_text
                    } else _binding!!.myDiary.text = "데이터가 없습니다!!"
                    _binding!!.youDiary.text = diary2_text

                    // Dday 계산하기
                    if(snapshot.child("Dday").value != null){
                        val coupleDay = fewDay(snapshot.child("Dday").value.toString())
                        Log.d("HomeFragment", "coupleDay : " + coupleDay)
                        if(coupleDay != null)
                            _binding!!.DdayCnt.text = "D+" + coupleDay.toString()
                        else if(coupleDay == null)
                            _binding!!.DdayCnt.text = "D+day"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun myData(){
        databaseRef = FirebaseDatabase.getInstance().reference

        myDiaryArrayList = arrayListOf<Diary>()
        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
//                    // Dday 계산하기
//                    if(snapshot.child("Dday").value != null){
//                        val coupleDay = fewDay(snapshot.child("Dday").value.toString())
//                        Log.d("HomeFragment", "coupleDay : " + coupleDay)
//                        if(coupleDay != null)
//                            _binding!!.DdayCnt.text = "D+" + coupleDay.toString()
//                        else if(coupleDay == null)
//                            _binding!!.DdayCnt.text = "D+day"
//                    }

                    if(snapshot.child("day").child(selectedDate).child("diary").getValue() != null) {
                        weather = (snapshot.child("day").child(selectedDate).child("weather").getValue() as Long).toInt()
                        emotions = (snapshot.child("day").child(selectedDate).child("emotions").getValue() as Long).toInt()
                        diary1_text = snapshot.child("day").child(selectedDate).child("diary").getValue() as String
                    }
                    else diary1_text = "데이터가 없습니다!!"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
        myDiaryArrayList.add(Diary(selectedDate, weather, emotions, diary1_text))
    }

    @SuppressLint("SimpleDateFormat")
    private fun fewDay(date : String) : Long{
        val db_sdf = SimpleDateFormat("yyyy.MM.dd")
        var meetDay = db_sdf.parse(date)?.time
        var today = dateFormat.parse(str_date)?.time

        return ( (today!! - meetDay!!) / (24 * 60 * 60 * 1000))
    }
}

