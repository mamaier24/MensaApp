package de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import de.hsulm.mensaapp.CONSTANTS.URLS;
import de.hsulm.mensaapp.JAVA_ID_AND_DATE_TIME.DateID;
import de.hsulm.mensaapp.R;

/**
 * Created by Marcel Maier on 30/11/18.
 * Adapter for RecyclerView - Food in UserAreaActivity
 */
public class FoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<FoodClass> food_list;
    private OnItemClickListener mListener;
    private static ImageView mImage;
    private static TextView mTitel;
    private static TextView mPreis;
    private static TextView mBewertung;
    private static RatingBar mRatingBar;
    private static TextView mCategory;
    private static TextView mDate;
    private static TextView mLastTime;
    private static DateID time = new DateID();

    public FoodAdapter(ArrayList<FoodClass> food_list) {
        this.food_list = food_list;
        //Empty food class needs to be added first for correct display in UserAreaActivity
        //First element in list must be zero because first field is another recycler card of type DATE
        food_list.add(0, new FoodClass());
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    public interface OnItemClickListener{
        void OnItemClick(int position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = null;

        switch (viewType) {

            case 1:
            listItem = layoutInflater.inflate(R.layout.recycler_date_view, parent, false);
            DateViewHolder mDateViewHolder = new DateViewHolder(listItem);
            return mDateViewHolder;

            case 2:
            listItem = layoutInflater.inflate(R.layout.recycler_view_item, parent, false);
            FoodViewHolder mFoodViewHolder = new FoodViewHolder(listItem, mListener);
            return mFoodViewHolder;

        }

        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType()==1){
            mDate.setText(time.getDate() + " - " + time.getDay());
        }else {
            FoodClass currentItem = food_list.get(position);
            String url = URLS.ROOT_URL_PICTURES + currentItem.getmimgId();
            new DownloadRecyclerImage(mImage).execute(url);
            mTitel.setText(currentItem.getName());
            mPreis.setText(currentItem.getPrice());
            mBewertung.setText(((Float) (((Integer) currentItem.getRating()).floatValue())).toString());
            mRatingBar.setRating(currentItem.getRating());
            mCategory.setText(currentItem.getCategory());
            mLastTime.setText(currentItem.getLastTime() + " Tag(en)");
        }
    }


    @Override
    public int getItemCount() {
        return food_list.size();
    }


    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 1;
        } else{
            return 2;
        }
    }


    public void clear() {
        final int size = food_list.size();
        food_list.clear();
        notifyItemRangeRemoved(0, size);
    }


    public static class FoodViewHolder extends RecyclerView.ViewHolder {

            public FoodViewHolder(View itemView, final OnItemClickListener listener) {
                super(itemView);

                mImage = (ImageView) itemView.findViewById(R.id.ivFavorite);
                mTitel = (TextView) itemView.findViewById(R.id.tvTitel);
                mPreis = (TextView) itemView.findViewById(R.id.tvPreis);
                mBewertung = (TextView) itemView.findViewById(R.id.tvBewertung);
                mRatingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
                mCategory = (TextView) itemView.findViewById(R.id.mCategory);
                mLastTime = (TextView) itemView.findViewById(R.id.mLastTime);

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


    public static class DateViewHolder extends RecyclerView.ViewHolder {

        public DateViewHolder(View itemView) {
            super(itemView);

            mDate = (TextView) itemView.findViewById(R.id.mDate);
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