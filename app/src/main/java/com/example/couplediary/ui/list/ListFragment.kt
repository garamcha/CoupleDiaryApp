package com.example.couplediary.ui.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.couplediary.MyDiaryAddActivity
import com.example.couplediary.MyDiaryEditActivity
import com.example.couplediary.R
import com.example.couplediary.databinding.FragmentListBinding
import com.example.couplediary.ui.home.Diary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ListFragment : Fragment() {
    // 감정 이미지 리스트
    private val emo = arrayListOf(R.drawable.ic_grinning, R.drawable.ic_neutral, R.drawable.ic_sadness,
    R.drawable.ic_disturb, R.drawable.ic_crying, R.drawable.ic_angry)
    // 날씨 이미지 리스트
    private val weath = arrayListOf(R.drawable.ic_sun, R.drawable.ic_cloud, R.drawable.ic_rain, R.drawable.ic_snow)

    private var _binding: FragmentListBinding? = null
    private lateinit var adapter : ListAdapter
    private lateinit var databaseRef : DatabaseReference
    lateinit var auth : FirebaseAuth

    private lateinit var dataMap: Map<*, *>
    private lateinit var recyclerView: RecyclerView

    private lateinit var dayDataList : ArrayList<String>
    var diaryArrayList: ArrayList<Diary> = arrayListOf<Diary>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference

        // 플로팅 버튼 클릭 시
        _binding!!.floatingActionButton.setOnClickListener {
            var intent = Intent(activity, MyDiaryAddActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data()

        val manager  = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.diary_list_recy)
        recyclerView.layoutManager = manager

        adapter = ListAdapter(diaryArrayList) // adapter 객체 생성
        _binding!!.diaryListRecy.adapter = adapter // Recycleview, adapter 등록
        recyclerView.adapter = adapter

        // Recycler View 역순 출력
        manager.reverseLayout = true
        manager.stackFromEnd = true
        _binding!!.diaryListRecy.layoutManager = manager

        // Recycler View item을 click 했을 때
        adapter.setOnItemClickListener(object : ListAdapter.OnItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                // 일기 수정화면으로 이동
                val intent = Intent(activity, MyDiaryEditActivity::class.java)

                intent.putExtra("position", position)
                intent.putExtra("dayDataList", dayDataList)
                startActivity(intent)
            }
        })

        // Recycler View item을 LongClick 했을 때
        adapter.setOnItemLongClickListener(object :ListAdapter.OnItemLongClickListener{
            override fun onLongClick(view: View, position: Int) {
                // Popup menu 생성
                val popup = PopupMenu(activity, view) // PopupMenu 객체 선언
                popup.menuInflater.inflate(R.menu.list_menu, popup.menu) // 메뉴 레이아웃 inflate

                // popup menu click 이벤트 처리
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    // onMenuItemClick 메소드 상태
                    when(it.itemId){
                        R.id.modify -> {
                            // 일기 수정화면으로 이동
                            val intent = Intent(activity, MyDiaryEditActivity::class.java)

                            intent.putExtra("position", position)
                            intent.putExtra("dayDataList", dayDataList)
                            startActivity(intent)
                        }
                        R.id.delete -> {
                            // 삭제 다이얼로그 띄우기
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("DELETE")
                                .setMessage("일기를 삭제하시겠습니까?")
                                .setPositiveButton("삭제") {
                                        dialog, id ->
                                    // 일기 리스트 삭제
                                    Log.d("ListFragment", "deleteDiary : " + dayDataList[position])
                                //Log.d("HomeFragment", "snapshot.ref" + snapshot.ref.child("day").child("2022-11-25"))
                                    deleteDate(dayDataList[position])

                                }
                                .setNegativeButton("취소") {
                                        dialog, id -> Toast.makeText(context, "취소", Toast.LENGTH_SHORT)
                                }
                            // 다이얼로그를 띄워주기
                            builder.show()
                        }
                    }
                    false
                })
                popup.show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun data(){
        databaseRef = FirebaseDatabase.getInstance().reference

        var data_diaryArrayList = arrayListOf<Diary>()
        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // DB의 day Data를 dayDataList에 저장
                for (snapshot in dataSnapshot.children) {
                    // 날짜별로 받아와서 DB -> Map -> ArrayList에 추가
                    dataMap = snapshot.child("day").getValue() as Map<*, *>
                    Log.d("ListFragment", "dataMap : " + dataMap)
                    dayDataList = dataMap.keys.toList() as ArrayList<String>
                    dayDataList.sort()

                    for(i in 0..dayDataList.size-1){
                        var weather : Int = (snapshot.child("day").child(dayDataList[i]).child("weather").getValue() as Long).toInt()
                        var emotion : Int = (snapshot.child("day").child(dayDataList[i]).child("emotions").getValue() as Long).toInt()
                        var diary : String = snapshot.child("day").child(dayDataList[i]).child("diary").getValue() as String

                        data_diaryArrayList.add(Diary(dayDataList[i], weather, emotion, diary))
                        diaryArrayList.add(Diary(dayDataList[i], weather, emotion, diary))

                    }
                    diaryArrayList = data_diaryArrayList
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun deleteDate(date : String){
        databaseRef = FirebaseDatabase.getInstance().reference

        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    if(snapshot.child("day").child(date).child("diary").getValue() != null) {
                        snapshot.ref.child("day").child(date).removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}