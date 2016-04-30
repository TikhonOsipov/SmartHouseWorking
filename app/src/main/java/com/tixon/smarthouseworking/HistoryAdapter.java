package com.tixon.smarthouseworking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tixon.smarthouseworking.model.ArduinoHistory;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by tikhon on 30.04.16
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private ArrayList<ArduinoHistory> history;
    private Calendar c;

    public HistoryAdapter(ArrayList<ArduinoHistory> list) {
        this.history = list;
        c = Calendar.getInstance();
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        holder.tvName.setText(history.get(position).getHistoryItem());
        c.setTimeInMillis(history.get(position).getTime());
        holder.tvTime.setText(context.getString(R.string.time_format, c));
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime;
        public HistoryViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.historyName);
            tvTime = (TextView) itemView.findViewById(R.id.historyTime);
        }
    }
}
