package edu.gatech.seclass.peersupport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<String> mData;
    private ItemClickListener mClickListener;

    public CourseAdapter(Context context, List<String> data) {
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
                .inflate(R.layout.course, parent, false);
        return new CourseAdapter.CourseHolder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CourseAdapter.CourseHolder) holder).bind(mData.get(position));
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

    private class CourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView course;

        CourseHolder(View view) {
            super(view);
            course = (TextView) view.findViewById(R.id.course);
            view.setOnClickListener(this);
        }

        void bind(String name) {
            String[] names = name.split("`");
            course.setText(names[0]);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
