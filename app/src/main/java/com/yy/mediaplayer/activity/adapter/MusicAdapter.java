package com.yy.mediaplayer.activity.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;
import com.yy.mediaplayer.utils.imageCache.BitmapUtil;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    List<MusicInfoEntity> listMusicInfo;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView msg;
        ConstraintLayout cl;
        ImageView more;
        ImageView album;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            msg = view.findViewById(R.id.msg);
            cl = view.findViewById(R.id.cl);
            more = view.findViewById(R.id.more);
            album = view.findViewById(R.id.iv);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(listMusicInfo.get(position).getName());
        holder.msg.setText(listMusicInfo.get(position).getArtist());
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onItemClickListener)
                    onItemClickListener.onItemClick(v, position);
            }
        });
        holder.cl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onItemClickListener)
                    onItemClickListener.onMoreClick(v, position);
            }
        });
        BitmapUtil.getInstance().disPlay(holder.album, listMusicInfo.get(position).getImageUrl());
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

        void onMoreClick(View v, int position);
    }
}
