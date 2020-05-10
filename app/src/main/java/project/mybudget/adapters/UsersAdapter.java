
package project.mybudget.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import project.mybudget.Admin.WalletsActivity;
import project.mybudget.Authentication.Login;
import project.mybudget.HomeActivity;
import project.mybudget.R;
import project.mybudget.models.person;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersAdapterViewHolder> {

    private ArrayList<person> mPersonData;
    String type;
    int bid;
    int primaryColor = ResourcesCompat.getColor(Login.context.getResources(), R.color.colorPrimary, null);

    public UsersAdapter(ArrayList mPersonData, String type, int bid) {
        this.mPersonData = mPersonData;
        this.type = type;
        this.bid = bid;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList getFilter(String query) {
        ArrayList adapterList = new ArrayList();
        for (person person : mPersonData) {
            if (person.getUsername().toLowerCase().equals(query.toLowerCase()))
                adapterList.add(person);
        }
        return adapterList;
    }

    public class UsersAdapterViewHolder extends RecyclerView.ViewHolder {


        TextView mUserTextView;
        Button sendInviteButton;
        FrameLayout userFrame;
        TextView sdate;
        CardView cardView;


        public UsersAdapterViewHolder(View view) {
            super(view);

            mUserTextView = (TextView) view.findViewById(R.id.textView_user);
            sendInviteButton = view.findViewById(R.id.send_invite_button);
            userFrame = view.findViewById(R.id.user_frame);
            sdate = view.findViewById(R.id.sdate_textview);
            cardView = view.findViewById(R.id.user_card);
        }
    }

    @Override
    public UsersAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.card_user, viewGroup, false);

        return new UsersAdapterViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(final UsersAdapterViewHolder userAdapterViewHolder, int position) {

        final person currentUser = mPersonData.get(position);

        userAdapterViewHolder.mUserTextView.setText(currentUser.getUsername() + "");


        userAdapterViewHolder.sendInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (userAdapterViewHolder.sendInviteButton.getText().equals(Login.context.getString(R.string.add_to_budget))) {
                    AsyncTaskRunner runner = new AsyncTaskRunner(currentUser, "add");
                    runner.execute(String.valueOf(currentUser.getPid()));
                    userAdapterViewHolder.sendInviteButton.setText(R.string.remove);
                    userAdapterViewHolder.sendInviteButton.setTextColor(Color.RED);
                    userAdapterViewHolder.userFrame.setBackgroundColor(Color.RED);
                } else {
                    AsyncTaskRunner runner = new AsyncTaskRunner(currentUser, "remove");
                    runner.execute(String.valueOf(currentUser.getPid()));
                    userAdapterViewHolder.sendInviteButton.setText(R.string.add_to_budget);
                    userAdapterViewHolder.userFrame.setBackgroundColor(primaryColor);
                    userAdapterViewHolder.sendInviteButton.setTextColor(primaryColor);
                }
            }
        });


        if (type.equals("current")) {
            userAdapterViewHolder.sendInviteButton.setText(R.string.remove);
            userAdapterViewHolder.sendInviteButton.setTextColor(Color.RED);
            userAdapterViewHolder.userFrame.setBackgroundColor(Color.RED);
        } else if (type.equals("admin")) {
            userAdapterViewHolder.sendInviteButton.setVisibility(View.INVISIBLE);
            userAdapterViewHolder.sdate.setText((currentUser.getCreate_time() + ""));
            userAdapterViewHolder.userFrame.setVisibility(View.INVISIBLE);

            userAdapterViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Login.context, WalletsActivity.class);
                    intent.putExtra("type", "current");
                    intent.putExtra("id", currentUser.getPid());
                    Login.context.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if (null == mPersonData) return 0;
        return mPersonData.size();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, person> {

        private person person;
        private String action;

        public AsyncTaskRunner(person person, String action) {
            this.person = person;
            this.action = action;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected person doInBackground(String... params) {

            try {
                Statement stmt = Login.stmt;
                String query;
                if (action.equalsIgnoreCase("add")) {
                    query = "INSERT INTO person_budgets (pid, bid, sdate) VALUES ('" + person.getPid() + "', '" + bid + "', '" + new java.sql.Date(System.currentTimeMillis()) + "')";
                    stmt.executeUpdate("UPDATE budget SET btid = 2 WHERE bid = " + bid);
                } else {
                    query = "DELETE FROM person_budgets WHERE (pid = " + person.getPid() + ") and (bid = " + bid + ")";
                }
                stmt.executeUpdate(query);

            } catch (SQLException e) { // handle to connect to the database server
                e.printStackTrace();
            }
            return person;
        }

        @Override
        protected void onPostExecute(person person) {
            super.onPostExecute(person);
            mPersonData.add(person);
            HomeActivity.updateUi();

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }


}