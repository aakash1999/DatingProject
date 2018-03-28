package com.example.sisirkumarnanda.ithappens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SISIR KUMAR NANDA on 07-03-2018.
 */

public class CardsAdapter extends ArrayAdapter<cards>{
    private static final String TAG = "CardsAdapter";

    Context mContext;

    public CardsAdapter(Context mContext, int resourceId, List<cards> items){
        super(mContext,resourceId,items);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        cards card = getItem(position);

        if(convertView==null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item,parent,false);
        }

        TextView name = (TextView)convertView.findViewById(R.id.name);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        name.setText(card.getName());
        image.setImageResource(R.mipmap.ic_launcher);

        return convertView;


    }
}
