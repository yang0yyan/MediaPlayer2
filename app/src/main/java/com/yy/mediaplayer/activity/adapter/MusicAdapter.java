package com.yy.mediaplayer.activity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    List<MusicInfoEntity> listMusicInfo;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView msg;
        ConstraintLayout cl;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            msg = view.findViewById(R.id.msg);
            cl = view.findViewById(R.id.cl);
        }

    }

    public MusicAdapter(List<MusicInfoEntity> listMusicInfo) {
        this.listMusicInfo = listMusicInfo;
    }

    public void setNewData(List<MusicInfoEntity> listMusicInfo) {
        this.listMusicInfo = listMusicInfo;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(listMusicInfo.get(position).getName());
        holder.msg.setText(listMusicInfo.get(position).getArtist());
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onItemClickListener)
                    onItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMusicInfo.size();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClick(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
