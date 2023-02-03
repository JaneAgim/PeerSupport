package edu.gatech.seclass.peersupport;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<String> mData;

    public MessageAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message, parent, false);
        return new MessageHolder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MessageHolder) holder).bind(mData.get(position));
    }

    String getItem(int id) {
        return mData.get(id);
    }


    private class MessageHolder extends RecyclerView.ViewHolder {
        TextView user;
        TextView time;
        TextView message;

        MessageHolder(View view) {
            super(view);
            user = (TextView) view.findViewById(R.id.message_user);
            time = (TextView) view.findViewById(R.id.message_time);
            message = (TextView) view.findViewById(R.id.message_text);
        }

        void bind(String string) {
            //Log.v("tokens", string);
            String[] tokens = string.split("`~`~`~`~`~`~`~`~");
            user.setText(tokens[1]);
            time.setText(tokens[2]);
            message.setText(tokens[0]);
        }
    }
}
