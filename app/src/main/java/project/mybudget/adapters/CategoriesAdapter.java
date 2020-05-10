
package project.mybudget.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import project.mybudget.R;
import project.mybudget.models.category;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesAdapterViewHolder> {

    private ArrayList<category> mCategoryData;

    public CategoriesAdapter(ArrayList<category> mCategoryData) {
        this.mCategoryData = mCategoryData;
    }

    public class CategoriesAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView mCategoryNameTextView;
        ImageView mCategoryImage;


        public CategoriesAdapterViewHolder(View view) {
            super(view);
            mCategoryNameTextView = (TextView) view.findViewById(R.id.category_text_view);
            mCategoryImage = (ImageView) view.findViewById(R.id.image);
        }

    }

    @Override
    public CategoriesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.card_category, viewGroup, false);
        return new CategoriesAdapterViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(CategoriesAdapterViewHolder categoriesAdapterViewHolder, int position) {

        category currentItem = mCategoryData.get(position);

        categoriesAdapterViewHolder.mCategoryNameTextView.setText(currentItem.getName());

        categoriesAdapterViewHolder.mCategoryImage.setBackgroundColor(currentItem.getColor());
//        categoriesAdapterViewHolder.mCategoryImage.setImageResource();


    }

    @Override
    public int getItemCount() {
        if (null == mCategoryData) return 0;
        return mCategoryData.size();
    }

}