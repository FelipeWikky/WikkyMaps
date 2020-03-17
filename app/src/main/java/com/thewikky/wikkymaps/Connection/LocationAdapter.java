package com.thewikky.wikkymaps.Connection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thewikky.wikkymaps.Model.LocationU;
import com.thewikky.wikkymaps.R;

import java.util.ArrayList;

public class LocationAdapter extends ArrayAdapter<LocationU> {

    private Context context;
    private ArrayList<LocationU> locs;

    public LocationAdapter(Context context, ArrayList<LocationU> objects) {
        super(context, 0, objects);
        this.context = context;
        this.locs = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if(locs != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_locs, parent, false);
            TextView txtNomeMarker = (TextView) view.findViewById(R.id.txtLstNomeMarker);
            TextView txtLatitudeMarker = (TextView) view.findViewById(R.id.txtLstLatitude);
            TextView txtLongitudeMarker = (TextView) view.findViewById(R.id.txtLstLongitude);

            LocationU lU = locs.get(position);
            txtNomeMarker.setText       ( "Nome: "      + lU.getNome() );
            txtLatitudeMarker.setText   ( "Latitude: "  + lU.getLatitude() );
            txtLongitudeMarker.setText  ( "Longitude: " + lU.getLongitude() );
        }
        return view;
    }
}
