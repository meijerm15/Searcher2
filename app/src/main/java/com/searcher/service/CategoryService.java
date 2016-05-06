package com.searcher.service;

import com.searcher.model.Category;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public interface CategoryService {

    @GET("category/all")
    Call<List<Category>> getCategories();
}
