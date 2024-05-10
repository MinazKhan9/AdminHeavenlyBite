package com.example.adminheavenlyfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.adminheavenlyfood.databinding.ActivityMainBinding
import com.example.adminheavenlyfood.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var completedOrderReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.addMenu.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
        binding.allItemMenu.setOnClickListener {
            val intent = Intent(this, AllItemActivity::class.java)
            startActivity(intent)
        }
        binding.outForDelivery.setOnClickListener {
            val intent = Intent(this, OutForDeliveryActivity::class.java)
            startActivity(intent)
        }
        binding.adminProfile.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }
        binding.createNewUserButton.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
        }
        binding.pendingOrderTextView.setOnClickListener {
            val intent = Intent(this, PendingOrderActivity::class.java)
            startActivity(intent)
        }
        binding.logOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        pendingOrders()
        completedOrders()
        wholeTimeEarning()
    }

    private fun wholeTimeEarning() {
        val listPfTotalPay = mutableListOf<Int>()
        completedOrderReference = FirebaseDatabase.getInstance().reference.child("CompletedOrder")
        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.totalPrice?.replace("$", "")?.toIntOrNull()
                        ?.let { i ->
                            listPfTotalPay.add(i)
                        }
                }
                binding.wholeTimeEarning.text = listPfTotalPay.sum().toString() + "$"
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun completedOrders() {
        val completedOrderReference = database.reference.child("CompletedOrder")
        var completeOrderItemCount = 0
        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                completeOrderItemCount = snapshot.childrenCount.toInt()
                binding.completeOrders.text = completeOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun pendingOrders() {
        database = FirebaseDatabase.getInstance()
        val pendingOrderReference = database.reference.child("OrderDetails")
        var pendingOrderItemCount = 0
        pendingOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingOrderItemCount = snapshot.childrenCount.toInt()
                binding.pendingOrders.text = pendingOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}