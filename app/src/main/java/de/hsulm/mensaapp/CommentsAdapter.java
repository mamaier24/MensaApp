package de.hsulm.mensaapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {


    private List<CommentsClass> listItems;
    private Context context;

    public CommentsAdapter(List<CommentsClass> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_comments, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommentsClass commentsClass = listItems.get(position);

        holder.tVComment_user.setText(commentsClass.getUser());
        //holder.tVComment_stars.setText(commentsClass.getStars());
        holder.tVComment_location.setText(commentsClass.getLocation());
        holder.tVComment_date.setText(commentsClass.getCDate());
        holder.tVComment_foodProfile.setText(commentsClass.getComment());
        holder.tVComment_rating.setText(commentsClass.getUserRating());

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tVComment_user;
        public TextView tVComment_rating;
        public TextView tVComment_location;
        public TextView tVComment_date;
        public TextView tVComment_foodProfile;

        public ViewHolder(View itemView) {
            super(itemView);

            tVComment_user = (TextView) itemView.findViewById(R.id.tVComment_user);
            tVComment_rating = (TextView) itemView.findViewById(R.id.tVComment_stars);
            tVComment_location = (TextView) itemView.findViewById(R.id.tVComment_location);
            tVComment_date = (TextView) itemView.findViewById(R.id.tVComment_date);
            tVComment_foodProfile = (TextView) itemView.findViewById(R.id.tvComment_foodProfile);
        }
    }


}
