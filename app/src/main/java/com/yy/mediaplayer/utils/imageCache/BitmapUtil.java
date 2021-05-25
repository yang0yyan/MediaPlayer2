package com.yy.mediaplayer.utils.imageCache;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.yy.mediaplayer.R;

public class BitmapUtil {
    private NetCacheUtil mNetCacheUtil;
    private LocalCacheUtil mLocalCacheUtil;
    private MemoryCacheUtil mMemoryCacheUtil;

    private static volatile BitmapUtil instance;

    public BitmapUtil(){
        mMemoryCacheUtil=new MemoryCacheUtil();
        mLocalCacheUtil=new LocalCacheUtil();
        mNetCacheUtil=new NetCacheUtil(mLocalCacheUtil,mMemoryCacheUtil);
    }

    public static BitmapUtil getInstance() {
        if (instance == null) {
            synchronized (BitmapUtil.class) {
                if (instance == null) {
                    instance = new BitmapUtil();
                }
            }
        }
        return instance;
    }

    public void disPlay(ImageView ivPic, String url) {
        if(null==url)return;
        ivPic.setImageResource(R.mipmap.ic_launcher);
        Bitmap bitmap;
        //内存缓存
        bitmap=mMemoryCacheUtil.getBitmapFromMemory(url);
        if (bitmap!=null){
            ivPic.setImageBitmap(bitmap);
            System.out.println("从内存获取图片啦.....");
            return;
        }

        //本地缓存
        bitmap = mLocalCacheUtil.getBitmapFromLocal(url);
        if(bitmap !=null){
            ivPic.setImageBitmap(bitmap);
            System.out.println("从本地获取图片啦.....");
            //从本地获取图片后,保存至内存中
            mMemoryCacheUtil.setBitmapToMemory(url,bitmap);
            return;
        }
        //网络缓存
        mNetCacheUtil.getBitmapFromNet(ivPic,url);
    }
    public Bitmap getBitmap(String url) {
        if(null==url)return null;
        Bitmap bitmap;
        //内存缓存
        bitmap=mMemoryCacheUtil.getBitmapFromMemory(url);
        if (bitmap!=null){
            System.out.println("从内存获取图片啦.....");
            return bitmap;
        }

        //本地缓存
        bitmap = mLocalCacheUtil.getBitmapFromLocal(url);
        if(bitmap !=null){
            System.out.println("从本地获取图片啦.....");
            //从本地获取图片后,保存至内存中
            mMemoryCacheUtil.setBitmapToMemory(url,bitmap);
            return bitmap;
        }
        return null;
    }
}
