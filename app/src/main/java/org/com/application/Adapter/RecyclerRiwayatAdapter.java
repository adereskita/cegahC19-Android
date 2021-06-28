package org.com.application.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.com.application.Interface.InterfaceDeleteRiwayat;
import org.com.application.Model.PostModel;
import org.com.application.Model.UsersCovidModel;
import org.com.application.ProfileActivity;
import org.com.application.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RecyclerRiwayatAdapter extends RecyclerView.Adapter<RecyclerRiwayatAdapter.ViewHolder> {

    private ArrayList<UsersCovidModel> data;
    private Context mContext;

    private int id;

    public RecyclerRiwayatAdapter(ArrayList<UsersCovidModel> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerRiwayatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_riwayat,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerRiwayatAdapter.ViewHolder holder, int position) {
        UsersCovidModel gejalaModel = data.get(position);

//        int nomor_riwayat;
//
//        for (nomor_riwayat = 0; nomor_riwayat < data.size(); nomor_riwayat++){
//
//        }

        String tgl = gejalaModel.getCreated_at().substring(0,10);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        String new_Date = null;
        try {
            new_Date = dateFormat.format(df.parse(tgl));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.gejala.setText(gejalaModel.getGejala());
        holder.tanggal_gejala.setText(new_Date);
        holder.no.setText(String.valueOf(position+1));

        holder.btn_hapus_riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int itemPosition = position;

                UsersCovidModel user = data.get(itemPosition);
                id = user.getId();
                if (mContext instanceof ProfileActivity){
                    ProfileActivity activity = (ProfileActivity)mContext;
                    activity.DeleteCovids(id);
                }

//
//
//                    if (mContext instanceof ProfileActivity){
//                        ((ProfileActivity) mContext).DeleteCovids(id);
//                    }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView no, gejala, tanggal_gejala;
        Button btn_hapus_riwayat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gejala = itemView.findViewById(R.id.gejala_riwayat);
            tanggal_gejala = itemView.findViewById(R.id.tanggal_riwayat);
            no = itemView.findViewById(R.id.no_riwayat);
            btn_hapus_riwayat = itemView.findViewById(R.id.hapus_riwayat);

//            btn_hapus_riwayat.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int itemPosition = getAbsoluteAdapterPosition();
//
//                    UsersCovidModel user = data.get(itemPosition);
//                    id = user.getId();
//
//
//                    if (mContext instanceof ProfileActivity){
//                        ((ProfileActivity) mContext).DeleteCovids(id);
//                    }
//                }
//            });
        }
    }
}
