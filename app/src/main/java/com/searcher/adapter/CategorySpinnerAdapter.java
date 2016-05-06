package com.searcher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.searcher.R;
import com.searcher.model.Category;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public class CategorySpinnerAdapter extends ArrayAdapter<Category> {
    private List<Category> categories;
    private LayoutInflater inflater;
    private Context context;

    public CategorySpinnerAdapter(Context context) {
        super(context, -1);
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CategorySpinnerAdapter(Context context, List<Category> categories) {
        this(context);
        this.categories = categories;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        return createCategoryView(position, view, parent);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        return createCategoryView(position, view, parent);
    }

    private View createCategoryView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.category_spinner_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        Category category = getItem(position);

        holder.mCategorySpinnerTextView.setText(category.getName());
        return view;
    }

    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    public class ViewHolder {

        @Bind(R.id.category_spinner_textview)
        protected TextView mCategorySpinnerTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public List<Category> getCategories() {
        return categories;
    }
}
