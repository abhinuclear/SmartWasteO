package com.project.smartwasteo.authority

//package com.project.smartwasteo.authority

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.project.smartwasteo.Complaint
//import com.project.smartwasteo.model.Complaint

class ComplaintViewModel : ViewModel() {
    private val _complaints = mutableStateOf<List<Complaint>>(emptyList())
    val complaints: State<List<Complaint>> = _complaints

    init {
        fetchComplaints()
    }

    private fun fetchComplaints() {
        val ref = FirebaseDatabase.getInstance().getReference("complaints")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<Complaint>()
                for (child in snapshot.children) {
                    val complaint = child.getValue(Complaint::class.java)
                    complaint?.let { tempList.add(it) }
                }
                _complaints.value = tempList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to fetch: ${error.message}")
            }
        })
    }
}
