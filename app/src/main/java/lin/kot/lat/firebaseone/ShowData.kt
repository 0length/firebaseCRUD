package lin.kot.lat.firebaseone

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import com.google.firebase.database.*
import lin.kot.lat.firebaseone.AdapterC.Adapter
import lin.kot.lat.firebaseone.Add_Data.Users

class ShowData : AppCompatActivity(){

    lateinit var refDb : DatabaseReference
    lateinit var list : MutableList<Users>
    lateinit var listview : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_data)

        refDb = FirebaseDatabase.getInstance().getReference("USERS")
        list = mutableListOf()
        listview = findViewById(R.id.listview)

        refDb.addValueEventListener( object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()){
                    list.clear()
                    for (h in p0.children){
                        val user = h.getValue(
                            Users::class.java
                        )
                        list.add(user!!)
                    }
                    val adapter = Adapter(this@ShowData, R.layout.show_user, list)
                    listview.adapter = adapter
                }
            }
        })

    }
}