package com.searcher.dao;

import com.searcher.model.Category;
import com.searcher.service.CategoryService;

import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public class CategoryDAO extends DAO<CategoryService> {

    @Override
    public CategoryService createService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(CategoryService.class);
    }

    public void getCategories(DAOCallback<List<Category>> callback) {
        enqueu(service.getCategories(), callback);
    }
}
