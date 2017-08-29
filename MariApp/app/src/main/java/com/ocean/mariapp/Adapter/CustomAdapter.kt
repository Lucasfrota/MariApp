package com.ocean.mariapp.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage
import com.ocean.mariapp.PerfilActivity
import com.ocean.mariapp.R

/**
 * Created by Luscas on 15/06/2017.
 */

class CustomAdapter(private val items: List<Item>, private val context: Context) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val item = items[position]

        holder.nome.text = item.nome
        holder.dataNascimento.text = item.dataNascimento
        holder.parentesco.text = item.parentesco

        val fotos = FirebaseStorage.getInstance()
        val storageRef = fotos.getReferenceFromUrl("gs://mariapp-7b3eb.appspot.com/")
        val fotoRef = storageRef.child(item.id + ".png")
        val ONE_MEGABYTE = (1024 * 1024).toLong()
        fotoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            holder.imageView.setImageBitmap(decodedByte)
        }.addOnFailureListener {

        }

        holder.listItem.setOnClickListener {
            val it = Intent(context, PerfilActivity::class.java)
            it.putExtra("id", item.id)
            context.startActivity(it)

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nome: TextView
        var dataNascimento: TextView
        var parentesco: TextView
        var imageView: de.hdodenhof.circleimageview.CircleImageView
        val listItem: android.support.constraint.ConstraintLayout

        init {

            nome = itemView.findViewById(R.id.nome) as TextView
            dataNascimento = itemView.findViewById(R.id.data_nascimento) as TextView
            parentesco = itemView.findViewById(R.id.parentesco) as TextView
            imageView = itemView.findViewById(R.id.imageViewLayout) as de.hdodenhof.circleimageview.CircleImageView
            listItem = itemView.findViewById(R.id.listItem) as android.support.constraint.ConstraintLayout

        }
    }
}