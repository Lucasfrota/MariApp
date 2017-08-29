package com.ocean.mariapp

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.PopupMenu
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_adicionar.*
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * A login screen that offers login via email/password.
 */
class AdicionarActivity : AppCompatActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    var mDateSetListener: DatePickerDialog.OnDateSetListener? = null

    var pref: SharedPreferences? = null

    var dataFormatada = ""

    private val RESULT_LOAD_IMG = 1

    val root = FirebaseDatabase.getInstance().reference

    val fotos = FirebaseStorage.getInstance()
    internal var storageRef = fotos.getReferenceFromUrl("gs://mariapp-7b3eb.appspot.com/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar)

        pref = this.getSharedPreferences("urlinf", 0)

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

        registrar.setOnClickListener {
            if(!nome.text.isEmpty() && !dataFormatada.isEmpty() && !parentesco.text.isEmpty()){
                regitrarParentes(this, nome.text.toString(), dataFormatada, parentesco.text.toString())

            }else{
                Toast.makeText(this, resources.getString(R.string.campo_vazio) , Toast.LENGTH_LONG).show()
            }
        }

        floatingActionButton.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG)
        }

    }

    fun regitrarParentes(context: Context, nomeParente: String, dataNascimentoParente: String, geracaoParente: String){

        val URL = pref!!.getString("url", "")

        val str_req = object : StringRequest(Request.Method.POST,  URL + "/mariapp/default/adicionar_parente",
                Response.Listener<String> { response ->
                    salvaImagem(response.toString())

                    val rand = Random()
                    val n = rand.nextInt(9)

                    var lugares = ArrayList<LatLng>()

                    lugares.add(LatLng(-3.034614, -60.038154))
                    lugares.add(LatLng(-3.051357, -59.907336))
                    lugares.add(LatLng(-3.122219, -60.011288))
                    lugares.add(LatLng(-3.122219, -60.011288))
                    lugares.add(LatLng(-3.060926, -60.090272))
                    lugares.add(LatLng(-3.298873, -60.606005))
                    lugares.add(LatLng(-22.794189, -43.457870))
                    lugares.add(LatLng(64.863277, -19.629521))
                    lugares.add(LatLng(36.106322, 127.839249))
                    lugares.add(LatLng(-29.388236, -50.841791))

                    root.child(response.toString()).child("lat").setValue(lugares.get(n).latitude)
                    root.child(response.toString()).child("lon").setValue(lugares.get(n).longitude)
                    root.child(response.toString()).child("nome").setValue(nomeParente)
                    Toast.makeText(context, resources.getString(R.string.adicionado_com_sucesso) , Toast.LENGTH_LONG).show()
                    finish()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(context, resources.getString(R.string.algo_errado) , Toast.LENGTH_LONG).show()
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("nome_parente", nomeParente)
                params.put("data_nascimento", dataNascimentoParente)
                params.put("geracao", geracaoParente)
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(str_req)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {

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

            circleImageView.setImageBitmap(bitmap)

            cursor.close()

        }catch (e: Exception){

        }
    }

    fun salvaImagem(nomeFoto: String){
        circleImageView.isDrawingCacheEnabled = true
        circleImageView.buildDrawingCache()
        val bmp = circleImageView.drawingCache
        val byteArrayOutputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        circleImageView.isDrawingCacheEnabled = false
        val byteArray = byteArrayOutputStream.toByteArray()

        val path = nomeFoto + ".png"
        val storageReference = fotos.getReference(path)

        storageReference.putBytes(byteArray)
    }

}

