package edu.gatech.seclass.peersupport;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CurrentContactsAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<String> mData;
    private ItemClickListener mClickListener;


    public CurrentContactsAdapter(Context context, List<String> data, boolean isOnline) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        String[] dataArray = mData.get(position).split("`");
        Log.v("viewType in adapter", dataArray[3]+"");
        if (dataArray[2].equalsIgnoreCase("true") && dataArray[3].equalsIgnoreCase("true")) {
            return 1;
        } else if (dataArray[2].equalsIgnoreCase("true") && dataArray[3].equalsIgnoreCase("false")) {
            return 2;
        } else if (dataArray[2].equalsIgnoreCase("false") && dataArray[3].equalsIgnoreCase("false")) {
            return 3;
        } else {
            return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_contact_online_read, parent, false);
        } else if(viewType == 2) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_contact_online_unread, parent, false);
        } else if(viewType == 3) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_contact_offline_unread, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_contact_offline_read, parent, false);
        }
        return new ContactHolder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /*boolean isOnline = false;
        String[] dataArray = mData.get(position).split("`");
        if (dataArray.length > 2){
            isOnline = dataArray[2].equalsIgnoreCase("true");
            Log.v("isOnline in OnBind", dataArray[2]+"");
        }
        View view;
        ViewGroup parent = ((ContactHolder) holder).getParent();
        if (!isOnline) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_contact, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_contact_online, parent, false);
            holder = new ContactHolder(view, parent);
        }*/
        ((ContactHolder) holder).bind(mData.get(position));
    }

    String getItem(int id) {
        return mData.get(id);
    }



    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClickCurrent(View view, int position);
    }

    private class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView contact;

        ContactHolder(View view) {
            super(view);
            contact = (TextView) view.findViewById(R.id.contact);
            view.setOnClickListener(this);
        }

        void bind(String name) {
            String[] names = name.split("`");
            contact.setText(names[0]);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClickCurrent(view, getAdapterPosition());
        }
    }
}
