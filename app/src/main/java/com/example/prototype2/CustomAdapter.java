package com.example.prototype2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter {

    public static final int TYPE_MY = 0;
    public static final int TYPE_THEIR = 1;
    public static final int TYPE_THEIR_VOICE = 2;

    private ListViewItem[] objects;

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return objects[position].getType();
    }

    public CustomAdapter(Context context, int resource, ListViewItem[] objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        ListViewItem listViewItem = objects[position];
        int listViewItemType = getItemViewType(position);

        if (convertView == null) {

            if (listViewItemType == TYPE_MY) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_message, null);
            } else if (listViewItemType == TYPE_THEIR) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.their_message, null);
            } else if (listViewItemType == TYPE_THEIR_VOICE) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.their_voice, null);
            }


            TextView textView = (TextView) convertView.findViewById(R.id.text);
            viewHolder = new ViewHolder(textView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.getText().setText(listViewItem.getText());

        return convertView;
    }

}