package lin.kot.lat.firebaseone.AdapterC

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import lin.kot.lat.firebaseone.Add_Data.Users
import lin.kot.lat.firebaseone.R
import lin.kot.lat.firebaseone.ShowData

class Adapter (val mCtx: Context, val layoutResId: Int, val list: List<Users>):ArrayAdapter<Users>(mCtx,layoutResId, list){
    override fun getView(position: Int, convertView: View?,parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view : View = layoutInflater.inflate(layoutResId, null)

        val textNama = view.findViewById<TextView>(R.id.textNama)
        val textStatus = view.findViewById<TextView>(R.id.textStatus)
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        val user = list[position]

        textNama.text = user.nama
        textStatus.text = user.status

        btnUpdate.setOnClickListener {
            showUpdateDialog(user)
        }
        btnDelete.setOnClickListener {
            Deleteinfo(user)
        }
        return  view
    }

    private fun Deleteinfo(user: Users){
        val progressDialog = ProgressDialog(context, R.style.AppTheme)
        progressDialog.isIndeterminate =true
        progressDialog.setMessage("Deleting")
        progressDialog.show()
        val mydatabase = FirebaseDatabase.getInstance()
            .getReference("USERS")
        mydatabase.child(user.id).removeValue()
        Toast.makeText(mCtx, "Deleted", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, ShowData::class.java)
        context.startActivity(intent)
    }

    private fun showUpdateDialog(user: Users){
        val builder = AlertDialog.Builder(mCtx)
        builder.setTitle("Update")
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.update_data, null)
        val textName = view.findViewById<EditText>(R.id.inputNama)
        val textStatus = view.findViewById<EditText>(R.id.inputStatus)
        textName.setText(user.nama)
        textStatus.setText(user.status)
        builder.setView(view)
        builder.setPositiveButton("Update"){
            dialog, which ->
            val dbUsers = FirebaseDatabase
                .getInstance().getReference("USERS")
            val nama = textName.text.toString().trim()
            val status = textStatus.text.toString().trim()
            if (nama.isEmpty()){
                textName.error = "Please enter name"
                textName.requestFocus()
                return@setPositiveButton
            }
            if (status.isEmpty()){
                textStatus.error = "please enter Status"
                textStatus.requestFocus()
                return@setPositiveButton
            }
            val user = Users(user.id, nama, status)

            dbUsers.child(user.id).setValue(user)
                .addOnCompleteListener {
                    Toast.makeText(mCtx, "Updated", Toast.LENGTH_SHORT).show()

                }
        }
        builder.setNegativeButton("No"){
            dialog, which ->
        }

        val alert = builder.create()
        alert.show()
    }
}

