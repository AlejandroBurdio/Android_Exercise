package com.example.alejandroburdiogarcia.android_exercise.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alejandroburdiogarcia.android_exercise.Models.User;
import com.example.alejandroburdiogarcia.android_exercise.R;
import java.util.List;

/**
 * Created by Alejandro Burdío on 21/11/2017.
 */

public class ListViewAdapter extends ArrayAdapter<User> {

    public ListViewAdapter(Context context, List<User> objects) {
        super(context, 0, objects);
    }

    static class ViewHolder {
        TextView tvId, tvName, tvDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con image_list_view.xml
            listItemView = inflater.inflate(
                    R.layout.listview_item,
                    parent,
                    false);
            holder = new ViewHolder();
            //Obteniendo instancias de los elementos
            holder.tvId = (TextView) listItemView.findViewById(R.id.id);
            holder.tvName = (TextView) listItemView.findViewById(R.id.name);
            holder.tvDate = (TextView) listItemView.findViewById(R.id.date);
            listItemView.setTag(holder);
        } else{
            holder = (ViewHolder)convertView.getTag();
        }

        //Obteniendo instancia de la Tarea en la posición actual
        User item = getItem(position);

        //Envía los datos
        holder.tvId.setText(String.valueOf(item.getId()));
        holder.tvName.setText(item.getName());
        holder.tvDate.setText(item.getBirthdate());

        return listItemView;
    }
}
