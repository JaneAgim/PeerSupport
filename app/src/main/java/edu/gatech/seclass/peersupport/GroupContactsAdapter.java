package edu.gatech.seclass.peersupport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupContactsAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<String> mData;
    private ItemClickListener mClickListener;

    public GroupContactsAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
        //Log.v("mData", Arrays.toString(mData.toArray()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_contact_offline_read, parent, false);
        return new GroupContactHolder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((GroupContactHolder) holder).bind(mData.get(position));
    }

    String getItem(int id) {
        return mData.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private class GroupContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView contact;

        GroupContactHolder(View view) {
            super(view);
            contact = (TextView) view.findViewById(R.id.contact);
            view.setOnClickListener(this);
        }

        void bind(String name) {
            String[] names = name.split("`");
            contact.setText(names[0] + ", " + names[1]);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
