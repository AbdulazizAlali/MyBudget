
package project.mybudget.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import project.mybudget.Authentication.Login;
import project.mybudget.R;
import project.mybudget.UsersActivity;
import project.mybudget.models.budget;

public class WalletsAdapter extends RecyclerView.Adapter<WalletsAdapter.WalletsAdapterViewHolder> {

    private ArrayList<budget> mbudgetData;
    // Class variables for the List that holds task data and the Context

    public WalletsAdapter(ArrayList mbudgetData) {
        this.mbudgetData = mbudgetData;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList getFilter(String query) {
        ArrayList adapterList = new ArrayList();
        for (budget budget : mbudgetData) {
            if (budget.getDescription().toLowerCase().contains(query.toLowerCase()) || query.equals(""))
                adapterList.add(budget);
        }
        return adapterList;
    }


    public class WalletsAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView mCategoryNAmeTextView;
        TextView mBalanceTextView;
        CardView cardView;
        TextView sdate;


        public WalletsAdapterViewHolder(View view) {
            super(view);
            mCategoryNAmeTextView = (TextView) view.findViewById(R.id.textView_category_name);
            mBalanceTextView = (TextView) view.findViewById(R.id.textView_budget);
            sdate = view.findViewById(R.id.sdate_textview);
            cardView = view.findViewById(R.id.wallet_card);
        }

    }

    @Override
    public WalletsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.card_wallet, viewGroup, false);
        return new WalletsAdapterViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(final WalletsAdapterViewHolder walletsAdapterViewHolder, int position) {

        final budget currentItem = mbudgetData.get(position);

        walletsAdapterViewHolder.mCategoryNAmeTextView.setText(currentItem.getDescription());
        walletsAdapterViewHolder.mBalanceTextView.setText(currentItem.getBalance() + "$");
        walletsAdapterViewHolder.sdate.setText((currentItem.getSdate() + ""));

//        walletsAdapterViewHolder.mBalanceLeftTextView.setText(currentItem.getPID()+"$");
//        walletsAdapterViewHolder.mBalanceSpentTextView.setText(currentItem.getValue()+"$");

        walletsAdapterViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.context, UsersActivity.class);
                intent.putExtra("type", "current");
                intent.putExtra("bid", currentItem.getBid());
                Login.context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (null == mbudgetData) return 0;
        return mbudgetData.size();
    }

}