package de.hsulm.mensaapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.GerichtViewHolder> {
    private ArrayList<FoodClass> mexampleList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
    mListener = listener;
}


    public FoodAdapter(ArrayList<FoodClass> exampleList) {
        mexampleList = exampleList;
    }


    @Override
    public GerichtViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.recycler_view_item, parent, false);
        GerichtViewHolder mGerichtViewHolder = new GerichtViewHolder(listItem, mListener);
        return mGerichtViewHolder;
    }

    @Override
    public void onBindViewHolder(GerichtViewHolder holder, int position) {
        FoodClass currentItem = mexampleList.get(position);
        new DownloadImageTask(holder.mImage).execute(Constants.ROOT_URL_PICTURES + currentItem.getmimgId());
        holder.mTitel.setText(currentItem.getName());
        holder.mPreis.setText(currentItem.getPrice());
        holder.mBewertung.setText(((Float)currentItem.getRating()).toString());
    }


    @Override
    public int getItemCount() {
        return mexampleList.size();
    }


    public void clear() {
        final int size = mexampleList.size();
        mexampleList.clear();
        notifyItemRangeRemoved(0, size);
    }


    public static class GerichtViewHolder extends RecyclerView.ViewHolder {


            ImageView mImage;
            TextView mTitel;
            TextView mPreis;
            TextView mBewertung;

            public GerichtViewHolder(View itemView, final OnItemClickListener listener) {
                super(itemView);

                mImage = (ImageView) itemView.findViewById(R.id.ivFavorite);
                mTitel = (TextView) itemView.findViewById(R.id.tvTitel);
                mPreis = (TextView) itemView.findViewById(R.id.tvPreis);
                mBewertung = (TextView) itemView.findViewById(R.id.tvBewertung);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null){
                            int position = getAdapterPosition();
                            if(position != RecyclerView.NO_POSITION){
                                listener.OnItemClick(position);
                            }
                        }
                    }
                });
            }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }


}