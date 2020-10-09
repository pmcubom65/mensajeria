package com.example.mensajesactividad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorContactos extends RecyclerView.Adapter<AdaptadorContactos.ViewHolder> {


    private ArrayList<Usuario> datos;


    public AdaptadorContactos(Context context, ArrayList<Usuario> list) {
        datos=list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView contacto1,contacto2;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contacto1=itemView.findViewById(R.id.contacto1);
            contacto2=itemView.findViewById(R.id.contacto2);

        }
    }


    @NonNull
    @Override
    public AdaptadorContactos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.holdercontactos, parent, false);

        return new ViewHolder(v);
    }





    @Override
    public void onBindViewHolder(@NonNull AdaptadorContactos.ViewHolder holder, int position) {
        holder.itemView.setTag(datos.get(position));
   //     holder.foto.setImageBitmap(datos.get(position).getFoto());
        holder.contacto1.setText(datos.get(position).getNombre().toString());
        holder.contacto2.setText(datos.get(position).getTelefono().toString());
    }

    @Override
    public int getItemCount()  {
        return datos.size();
    }
}
