package edu.gatech.seclass.peersupport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OtherContactsAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<String> mData;
    private ItemClickListener mClickListener;

    public OtherContactsAdapter(Context context, List<String> data) {
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
        if (dataArray.length > 2 && dataArray[2].equalsIgnoreCase("true")) {
            return 1;
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
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_contact_offline_read, parent, false);
        }
        return new ContactHolder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ContactHolder) holder).bind(mData.get(position));
    }

    String getItem(int id) {
        return mData.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClickOther(View view, int position);
    }

    private class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
            if (mClickListener != null) mClickListener.onItemClickOther(view, getAdapterPosition());
        }
    }
}
