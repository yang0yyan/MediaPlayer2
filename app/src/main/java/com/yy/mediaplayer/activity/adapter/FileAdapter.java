package com.yy.mediaplayer.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.yy.mediaplayer.R;

import java.util.List;
import java.util.Map;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    List<Map<String,String>> listFileInfo;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        CheckBox cb;
        ConstraintLayout item;

        public ViewHolder (View view)
        {
            super(view);
            name = view.findViewById(R.id.tv_file_name);
            cb = view.findViewById(R.id.cb_file_choose);
            item = view.findViewById(R.id.item);
        }

    }

    public FileAdapter(List<Map<String,String>> listMusicInfo) {
        this.listFileInfo = listMusicInfo;
    }

    public void setNewData(List<Map<String,String>> listMusicInfo){
        this.listFileInfo = listMusicInfo;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(listFileInfo.get(position).get("name"));
        holder.cb.setChecked(listFileInfo.get(position).get("check").equals("1"));
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listFileInfo.get(position).put("check",isChecked?"1":"0");
            }
        });
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=onItemClickListener)
                    onItemClickListener.onClick(v,position);
            }
        });
    }
    OnItemClickListener onItemClickListener;
    public void setOnItemClick(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return listFileInfo.size();
    }

    public interface OnItemClickListener{
        void onClick(View v,int position);
    }
}
