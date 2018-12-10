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
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private ArrayList<FoodClass> mexampleList;
    private OnItemClickListener mListener;

    public FoodAdapter(ArrayList<FoodClass> exampleList)
    {
        this.mexampleList = exampleList;
    }


    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }


    public interface OnItemClickListener{
        void OnItemClick(int position);
    }


    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.recycler_view_item, parent, false);
        FoodViewHolder mFoodViewHolder = new FoodViewHolder(listItem, mListener);
        return mFoodViewHolder;
    }


    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        FoodClass currentItem = mexampleList.get(position);
        new DownloadRecyclerImage(holder.mImage).execute(Constants.ROOT_URL_PICTURES + currentItem.getmimgId());
        holder.mTitel.setText(currentItem.getName());
        holder.mPreis.setText(currentItem.getPrice());
        holder.mBewertung.setText(((Integer)currentItem.getRating()).toString());
        holder.mRatingBar.setRating(currentItem.getRating());
        holder.mCategory.setText(currentItem.category);
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


    public static class FoodViewHolder extends RecyclerView.ViewHolder {
            ImageView mImage;
            TextView mTitel;
            TextView mPreis;
            TextView mBewertung;
            RatingBar mRatingBar;
            TextView mCategory;

            public FoodViewHolder(View itemView, final OnItemClickListener listener) {
                super(itemView);

                mImage = (ImageView) itemView.findViewById(R.id.ivFavorite);
                mTitel = (TextView) itemView.findViewById(R.id.tvTitel);
                mPreis = (TextView) itemView.findViewById(R.id.tvPreis);
                mBewertung = (TextView) itemView.findViewById(R.id.tvBewertung);
                mRatingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
                mCategory = (TextView) itemView.findViewById(R.id.mCategory);

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


    private class DownloadRecyclerImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadRecyclerImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                mIcon11 = null;
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bmImage.setImageBitmap(result);
                bmImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }

}