package com.taxioperadora.APIChoferes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taxioperadora.R;

import java.util.ArrayList;

/**
 * Created by carlos on 22/02/17.
 */

public class MyAdapterList extends ArrayAdapter<ObjectDriver> {

    ArrayList<ObjectDriver> driverList;
    Context context;
    private LayoutInflater mInflater;

    // Constructors
    public MyAdapterList(Context context, ArrayList<ObjectDriver> objects) {
        super(context, 0, objects);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        driverList = objects;
    }

    @Override
    public ObjectDriver getItem(int position) {
        return driverList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.adapter_location_drivers, parent, false);
            vh = ViewHolder.create((RelativeLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        ObjectDriver item = getItem(position);

        vh.textViewIdUbicacion.setText(item.getID_UBICACION());
        vh.textViewIdChofer.setText(item.getID_CHOFER());
        vh.imageView.setImageResource(R.drawable.ic_drivers);
        return vh.rootView;
    }

    private static class ViewHolder {
        public final RelativeLayout rootView;
        public final ImageView imageView;
        public final TextView textViewIdUbicacion;
        public final TextView textViewIdChofer;

        private ViewHolder(RelativeLayout rootView, ImageView imageView, TextView textViewName, TextView textViewEmail) {
            this.rootView = rootView;
            this.imageView = imageView;
            this.textViewIdUbicacion = textViewName;
            this.textViewIdChofer = textViewEmail;
        }

        public static ViewHolder create(RelativeLayout rootView) {
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            TextView textViewIdUbicacion = (TextView) rootView.findViewById(R.id.textViewIdUbicacion);
            TextView textViewIdChofer = (TextView) rootView.findViewById(R.id.textViewIdChofer);
            return new ViewHolder(rootView, imageView, textViewIdUbicacion, textViewIdChofer);
        }
    }
}