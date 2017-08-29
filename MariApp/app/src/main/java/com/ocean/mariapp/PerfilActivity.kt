package com.ocean.mariapp

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.PopupMenu
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_perfil.*
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.util.*

class PerfilActivity : AppCompatActivity() {

    var pref: SharedPreferences? = null

    var dataNascimento: String? = null
    var nomeParente: String? = null
    var geracaoParente: String? = null

    var id = ""

    var mDateSetListener: DatePickerDialog.OnDateSetListener? = null
    var dataFormatada = ""

    val fotos = FirebaseStorage.getInstance()

    private val RESULT_LOAD_IMG = 1

    val root = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        pref = this.getSharedPreferences("urlinf", 0)

        val extras = intent.extras

        id = extras.getString("id")

        val fotos = FirebaseStorage.getInstance()
        val storageRef = fotos.getReferenceFromUrl("gs://mariapp-7b3eb.appspot.com/")
        val fotoRef = storageRef.child(id + ".png")
        val ONE_MEGABYTE = (1024 * 1024).toLong()
        fotoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageViewLayout.setImageBitmap(decodedByte)
        }.addOnFailureListener {

        }

        getParentes(this)

        geracao.setOnClickListener {
            val popupMenu = PopupMenu(this, geracao)
            popupMenu.menuInflater.inflate(R.menu.floating_contextual_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->

                parentesco.text = item.title

                return@setOnMenuItemClickListener true
            }

            popupMenu.show()
        }

        datePicker.setOnClickListener {
            val cal = Calendar.getInstance()

            var ano = cal.get(Calendar.YEAR)
            var mes = cal.get(Calendar.MONTH)
            var dia = cal.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    ano,mes,dia)

            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        mDateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            dataFormatada = year.toString() + "-" + (month + 1).toString() + "-" + dayOfMonth.toString()
            dataDeNascimento.text = dayOfMonth.toString() + "/" + (month + 1).toString() + "/" + year.toString()
        }

        deletarParente.setOnClickListener {
            val dig = AlertDialog.Builder(this@PerfilActivity)
            dig.setTitle(R.string.atencao)
            dig.setMessage(R.string.deseja_apagar)
            dig.setPositiveButton(R.string.sim, DialogInterface.OnClickListener { dialog, which -> deleteParente(this) })
            dig.setNegativeButton(R.string.nao, null)
            dig.show()
        }

        salvar.setOnClickListener {
            updateParente(this)
        }

        floatingActionButton.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG)
        }
    }

    fun getParentes(context: Context){

        val URL = pref!!.getString("url", "")

        val str_req = object : StringRequest(Request.Method.POST,  URL + "/mariapp/default/get_parente",
                Response.Listener<String> { response ->
                    try {

                        val json = JSONArray(response.trim())

                        dataFormatada = json.getJSONObject(0).getString("data_nascimento")
                        dataNascimento = getData(dataFormatada)
                        nomeParente = json.getJSONObject(0).getString("nome_parente")
                        geracaoParente = json.getJSONObject(0).getString("geracao")

                        parentesco.text = geracaoParente
                        nome.setText(nomeParente)
                        dataDeNascimento.text = dataNascimento

                    } catch (e: JSONException) {
                        Toast.makeText(context, e.toString() , Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, resources.getString(R.string.algo_errado) , Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("id", id)
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(str_req)
    }

    fun deleteParente(context: Context){

        val URL = pref!!.getString("url", "")

        val str_req = object : StringRequest(Request.Method.POST,  URL + "/mariapp/default/delete_parente",
                Response.Listener<String> { response ->
                    try {
                        Toast.makeText(context, resources.getString(R.string.parente_deletado) , Toast.LENGTH_LONG).show()
                        root.child(id).setValue(null)
                        finish()
                    } catch (e: JSONException) {

                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, resources.getString(R.string.algo_errado) , Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("id", id)
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(str_req)
    }

    fun updateParente(context: Context){

        val URL = pref!!.getString("url", "")

        val str_req = object : StringRequest(Request.Method.POST,  URL + "/mariapp/default/update_parente",
                Response.Listener<String> { response ->
                    try {
                        salvaImagem(id)
                        Toast.makeText(context, resources.getString(R.string.salvar) , Toast.LENGTH_LONG).show()
                        finish()
                    } catch (e: JSONException) {

                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, resources.getString(R.string.algo_errado) , Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("id", id)
                params.put("nome_parente", nome.text.toString())
                params.put("data_nascimento", dataFormatada)
                params.put("geracao", parentesco.text.toString())
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(str_req)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try{
            val pickedImage = data!!.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(pickedImage, filePath, null, null, null)
            cursor!!.moveToFirst()
            val imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]))

            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            var bitmap = BitmapFactory.decodeFile(imagePath, options)

            //corta a foto
            if (bitmap.width >= bitmap.height) {

                bitmap = Bitmap.createBitmap(
                        bitmap,
                        bitmap.width / 2 - bitmap.height / 2,
                        0,
                        bitmap.height,
                        bitmap.height
                )

            } else {

                bitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        bitmap.height / 2 - bitmap.width / 2,
                        bitmap.width,
                        bitmap.width
                )
            }

            imageViewLayout.setImageBitmap(bitmap)

            cursor.close()

        }catch (e: Exception){

        }
    }

    fun salvaImagem(nomeFoto: String){
        imageViewLayout.isDrawingCacheEnabled = true
        imageViewLayout.buildDrawingCache()
        val bmp = imageViewLayout.drawingCache
        val byteArrayOutputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        imageViewLayout.isDrawingCacheEnabled = false

        val byteArray = byteArrayOutputStream.toByteArray()

        val path = nomeFoto + ".png"
        val storageReference = fotos.getReference(path)

        storageReference.putBytes(byteArray)
    }

    fun getData(data: String): String{

        var saida = data.substring(8,10) + "/" + data.substring(5,7) + "/" + data.substring(0,4)

        return saida
    }
}
