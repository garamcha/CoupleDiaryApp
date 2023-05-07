package com.example.couplediary.ui.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.couplediary.ConnectActivity
import com.example.couplediary.DDayActivity
import com.example.couplediary.LoginActivity
import com.example.couplediary.databinding.FragmentSettingBinding
import com.example.couplediary.ui.home.Diary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private lateinit var auth: FirebaseAuth //파이어 베이스 인증 처리

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // DB에서 data 받아와 뿌려주기
        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val coupleCode = snapshot.key
                    _binding!!.coupleCode.text = coupleCode.toString()
                    val date = snapshot.child("Dday").value.toString()
                    _binding!!.firstday.text = date
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        // 처음만난날 클릭 시 액티비티 이동
        _binding!!.dday.setOnClickListener {
            var intent = Intent(activity, DDayActivity::class.java)
            startActivity(intent)
        }

        // 커플연결하기 클릭 시 액티비티 이동
        _binding!!.connect.setOnClickListener {
            var intent = Intent(activity, ConnectActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 클릭 시 Popup 다이얼로그 창 띄우기
        _binding!!.logout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("LOGOUT")
                .setMessage("커플다이어리를 로그아웃 하시겠습니까?")
                .setPositiveButton("확인") {
                        dialog, id ->
                    auth.signOut() // 로그아웃
                    // 로그인 화면으로 이동
                    var intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .setNegativeButton("취소") {
                        dialog, id -> Toast.makeText(context, "취소", Toast.LENGTH_SHORT)
                }
            // 다이얼로그를 띄워주기
            builder.show()
        }
        // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}