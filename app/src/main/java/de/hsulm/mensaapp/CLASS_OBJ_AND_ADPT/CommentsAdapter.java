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

            holder.tvCommentUser.setText("");
            holder.tvCommentLocation.setText("");
            holder.tvCommentDate.setText("");
            holder.tvTitle.setText("Noch kein Kommentar vorhanden! Sei der erste der einen Kommentar abgibt!");
            holder.tvCommentRating.setText("");
            holder.ivIconCommentUser.setVisibility(View.GONE);
            holder.ivIconCommentDate.setVisibility(View.GONE);
            holder.ivIconCommentLocation.setVisibility(View.GONE);
            holder.ivIconCommentRating.setVisibility(View.GONE);

        }else{
            CommentsClass commentsClass = listItems.get(position);
            holder.tvCommentUser.setText(commentsClass.getUser());
            holder.tvCommentLocation.setText(commentsClass.getLocation());
            holder.tvCommentDate.setText(commentsClass.getCDate());
            holder.tvTitle.setText(commentsClass.getComment());
            holder.tvCommentRating.setText(commentsClass.getUserRating());
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

        public TextView tvCommentUser;
        public TextView tvCommentRating;
        public TextView tvCommentLocation;
        public TextView tvCommentDate;
        public TextView tvTitle;
        public ImageView ivIconCommentUser;
        public ImageView ivIconCommentDate;
        public ImageView ivIconCommentLocation;
        public ImageView ivIconCommentRating;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCommentUser = (TextView) itemView.findViewById(R.id.tvCommentUser);
            tvCommentRating = (TextView) itemView.findViewById(R.id.tvCommentRating);
            tvCommentLocation = (TextView) itemView.findViewById(R.id.tvCommentLocation);
            tvCommentDate = (TextView) itemView.findViewById(R.id.tvCommentDate);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivIconCommentUser = (ImageView) itemView.findViewById(R.id.ivIconCommentUser);
            ivIconCommentDate = (ImageView) itemView.findViewById(R.id.ivIconCommentDate);
            ivIconCommentLocation = (ImageView) itemView.findViewById(R.id.ivIconCommentLocation);
            ivIconCommentRating = (ImageView) itemView.findViewById(R.id.ivIconCommentRating);
        }
    }


}
