package org.com.application.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.com.application.ArticleActivity;
import org.com.application.HomeActivity;
import org.com.application.LoginActivity;
import org.com.application.Model.PostModel;
import org.com.application.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<PostModel> data;
    private Context mContext;

    private final int limit = 5;
    private String id;

    public RecyclerViewAdapter(ArrayList<PostModel> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_list, parent,false);

            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostModel nPost = data.get(position);

        String tgl = nPost.getCreated_at().substring(0,10);
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        String new_Date = null;
        try {
            new_Date = dateFormat.format(df.parse(tgl));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.title.setText(nPost.getTitle());
        holder.date.setText(new_Date);
        Picasso.get().load(HomeActivity.URL_BASE_STORAGE+nPost.getImage()).into(holder.imgView);

        id = nPost.getId();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, date,body;
        private ImageView imgView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_rv_title);
            date = itemView.findViewById(R.id.tv_rc_date);
            imgView = itemView.findViewById(R.id.img_home_post);

            itemView.isClickable();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = getAbsoluteAdapterPosition();

                    PostModel nPostId = data.get(itemPosition);
                    id = nPostId.getId();

                    Intent i = new Intent(mContext.getApplicationContext(), ArticleActivity.class);
                    i.putExtra("EXTRA_ID_POST",id);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.getApplicationContext().startActivity(i);
                }
            });
        }
    }
}
