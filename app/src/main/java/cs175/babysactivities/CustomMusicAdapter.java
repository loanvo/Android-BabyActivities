package cs175.babysactivities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Loan Vo on 12/7/17.
 */

public class CustomMusicAdapter extends BaseAdapter{
    private Context context;
    private int layout;
    private ArrayList<Music> arrayList;

    public CustomMusicAdapter(Context context, int layout, ArrayList<Music> arrayList){
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder{
        TextView textName;
        ImageView playButn, stopButn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(layout, null);
           // viewHolder.textName = (TextView) convertView.findViewById(R.id.song_name);
            //viewHolder.playButn = (ImageView) convertView.findViewById(R.id.play);
            //viewHolder.stopButn = (ImageView) convertView.findViewById(R.id.stop);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Music music = arrayList.get(position);
        viewHolder.textName.setText(music.getName());

        return convertView;
    }
}
