package project.mybudget.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import project.mybudget.Authentication.Login;
import project.mybudget.R;
import project.mybudget.models.receiptView;

public class ReceiptsAdapter extends RecyclerView.Adapter<ReceiptsAdapter.ReceiptAdapterViewHolder> {

    private ArrayList<receiptView> mReceiptData;
    private ReceiptAdapterViewHolder receiptAdapterViewHolder;

    public ReceiptsAdapter(ArrayList<receiptView> mReceiptData) {
        this.mReceiptData = mReceiptData;
    }


    public class ReceiptAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTextView;
        TextView mAmountTextView;
        TextView mDayTextView;
        TextView mCategoryTextView;
        TextView mMonthTextView;
        TextView mUserTextView;
        ImageView imageView;
        TextView mBudgetTextView;


        public ReceiptAdapterViewHolder(View view) {
            super(view);
            mNameTextView = (TextView) view.findViewById(R.id.textview_receipt_name);
            mAmountTextView = (TextView) view.findViewById(R.id.textView_amount);
            mDayTextView = (TextView) view.findViewById(R.id.textView_day_of_month);
            mCategoryTextView = (TextView) view.findViewById(R.id.textView_category);
            mBudgetTextView = (TextView) view.findViewById(R.id.textView_budget);
            mMonthTextView = (TextView) view.findViewById(R.id.textView_month);
            mUserTextView = (TextView) view.findViewById(R.id.textView_user);
            imageView = view.findViewById(R.id.image);
        }
    }

    @Override
    public ReceiptAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.card_receipt, viewGroup, false);
        return new ReceiptAdapterViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ReceiptAdapterViewHolder receiptAdapterViewHolder, int position) {

        receiptView currentItem = mReceiptData.get(position);

        receiptAdapterViewHolder.mNameTextView.setText(currentItem.getDescription());
        receiptAdapterViewHolder.mAmountTextView.setText(currentItem.getValue() +" "+ Login.context.getString(R.string.sr));


        receiptAdapterViewHolder.mAmountTextView.setTextColor(currentItem.getColor());

        receiptAdapterViewHolder.imageView.setColorFilter(currentItem.getCatColor());
        receiptAdapterViewHolder.mCategoryTextView.setText(currentItem.getCatName());
        String username = currentItem.getUserName();
        if (username.contains("@"))
            username = username.substring(0, username.lastIndexOf('@'));
        receiptAdapterViewHolder.mUserTextView.setText(username);
        receiptAdapterViewHolder.mBudgetTextView.setText(currentItem.getBudgetName());
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentItem.getRectime());
        String month = new DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH)];
        receiptAdapterViewHolder.mMonthTextView.setText(month);
        receiptAdapterViewHolder.mDayTextView.setText(cal.get(Calendar.DAY_OF_MONTH) + "");

    }

    @Override
    public int getItemCount() {
        if (null == mReceiptData) return 0;
        return mReceiptData.size();
    }
}