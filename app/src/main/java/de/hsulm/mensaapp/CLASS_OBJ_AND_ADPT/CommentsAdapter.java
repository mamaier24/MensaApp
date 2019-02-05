package de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hsulm.mensaapp.R;

/**
 * Created by Marcel Maier on 30/11/18.
 * Adapter for RecyclerView - Comment fields in FoodProfile
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<CommentsClass> listItems;
    private Context context;
    private boolean isDefault;

    public CommentsAdapter(boolean isDefault, Context context) {
        this.isDefault = isDefault;
        this.context = context;
    }


    public CommentsAdapter(boolean isDefault, Context context, List<CommentsClass> listItems) {
        this.isDefault = isDefault;
        this.context = context;
        this.listItems = listItems;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_comments, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        if(isDefault == true) {

            holder.tVComment_user.setText("");
            holder.tVComment_location.setText("");
            holder.tVComment_date.setText("");
            holder.tVComment_foodProfile.setText("Noch kein Kommentar vorhanden! Sei der erste der einen Kommentar abgibt!");
            holder.tVComment_rating.setText("");
            holder.tvComment_userPicture.setVisibility(View.GONE);
            holder.tvComment_datePicture.setVisibility(View.GONE);
            holder.tvComment_locationPicture.setVisibility(View.GONE);
            holder.tvComment_ratingPicture.setVisibility(View.GONE);

        }else{
            CommentsClass commentsClass = listItems.get(position);
            holder.tVComment_user.setText(commentsClass.getUser());
            holder.tVComment_location.setText(commentsClass.getLocation());
            holder.tVComment_date.setText(commentsClass.getCDate());
            holder.tVComment_foodProfile.setText(commentsClass.getComment());
            holder.tVComment_rating.setText(commentsClass.getUserRating());
        }

    }

    @Override
    public int getItemCount() {
        if (isDefault == true){
            return 1;
        }else {
            return listItems.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tVComment_user;
        public TextView tVComment_rating;
        public TextView tVComment_location;
        public TextView tVComment_date;
        public TextView tVComment_foodProfile;
        public ImageView tvComment_userPicture;
        public ImageView tvComment_datePicture;
        public ImageView tvComment_locationPicture;
        public ImageView tvComment_ratingPicture;

        public ViewHolder(View itemView) {
            super(itemView);

            tVComment_user = (TextView) itemView.findViewById(R.id.tVComment_user);
            tVComment_rating = (TextView) itemView.findViewById(R.id.tVComment_stars);
            tVComment_location = (TextView) itemView.findViewById(R.id.tVComment_location);
            tVComment_date = (TextView) itemView.findViewById(R.id.tVComment_date);
            tVComment_foodProfile = (TextView) itemView.findViewById(R.id.tvComment_foodProfile);
            tvComment_userPicture = (ImageView) itemView.findViewById(R.id.imageView6);
            tvComment_datePicture = (ImageView) itemView.findViewById(R.id.imageView7);
            tvComment_locationPicture = (ImageView) itemView.findViewById(R.id.imageView3);
            tvComment_ratingPicture = (ImageView) itemView.findViewById(R.id.imageView4);
        }
    }


}
