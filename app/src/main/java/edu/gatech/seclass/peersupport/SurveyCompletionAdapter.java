//package edu.gatech.seclass.peersupport;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class SurveyCompletionAdapter extends RecyclerView.Adapter {
//    private Context mContext;
//    private List<String> studentData;
//    private ItemClickListener studentClickListener;
//
//    public SurveyCompletionAdapter(Context context, List<String> studentData) {
//        mContext = context;
//        studentData = studentData;
//    }
//
//    @Override
//    public int getItemCount() {
//        return studentData.size();
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.activity_survey_completion, parent, false);
//        return new SurveyCompletionAdapter.SurveyCompletionActivity(view);
//    }
//
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        ((CurrentContactsAdapter.ContactHolder) holder).bind(mData.get(position));
//    }
//
//    public interface ItemClickListener {
//        void onItemClickCurrent(View view, int position);
//    }
//
//    private class StudentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        TextView student;
//
//
//    }
//}
