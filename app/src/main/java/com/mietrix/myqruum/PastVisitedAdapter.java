package com.mietrix.myqruum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PastVisitedAdapter extends RecyclerView.Adapter<PastVisitedAdapter.PastVisitedViewHolder> {


    private Context mCtx;
    private List<Log> logList;
    private OnItemClicked onClick;


    public interface OnItemClicked {
        void onItemClick(int position);
    }

    public PastVisitedAdapter(Context mCtx, List<Log> logList) {
        this.mCtx = mCtx;
        this.logList = logList;
    }

    @Override
    public PastVisitedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.past_visited_list, null);
        return new PastVisitedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PastVisitedViewHolder holder,final int position) {
        Log log = logList.get(position);


         //getName
        holder.placeName.setText(log.getPlaceName()); //GetICnum
        holder.logStatus.setText(log.getLogStatus());

        if("Inside".equalsIgnoreCase(log.getLogStatus())){
            holder.enterDate.setText("Enter On "+log.getEnterDate()+" "+log.getEnterTime());
            holder.test.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.colorNavIcon));
            holder.logStatus.setTextColor(ContextCompat.getColor(mCtx,R.color.colorLightGreen));
        }else if("Visited".equalsIgnoreCase(log.getLogStatus())){
            holder.enterDate.setText("Visited On "+log.getEnterDate()+" "+log.getEnterTime());
            holder.logStatus.setTextColor(ContextCompat.getColor(mCtx,R.color.red));
        }

        holder.test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    class PastVisitedViewHolder extends RecyclerView.ViewHolder {

        TextView enterDate, placeName,logStatus;
        // ImageView imageView;
        RelativeLayout test;

        public PastVisitedViewHolder(View itemView) {
            super(itemView);

            test=itemView.findViewById(R.id.testing);
            enterDate = itemView.findViewById(R.id.enterDate);
            placeName = itemView.findViewById(R.id.placeName);
            logStatus = itemView.findViewById(R.id.logStatus);
        }
    }
    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}
