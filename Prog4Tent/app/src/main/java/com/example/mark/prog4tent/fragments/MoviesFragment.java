package com.example.mark.prog4tent.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mark.prog4tent.MainActivity;
import com.example.mark.prog4tent.R;
import com.example.mark.prog4tent.adapter.MovieAdapter;
import com.example.mark.prog4tent.domain.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark on 15-6-2017.
 */

public class MoviesFragment extends Fragment {

    public static final String PREFS_NAME_TOKEN = "Prefsfile";
    private SharedPreferences preferences;
    private ListView movieList;
    private ArrayList<Movie> movieArrayList;
    private ArrayAdapter movieAdapter;
    private String ip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Movies");

        movieList = (ListView) getView().findViewById(R.id.movies_listview);
        movieArrayList = new ArrayList<>();
        movieAdapter = new MovieAdapter(getContext(), movieArrayList);
        movieList.setAdapter(movieAdapter);

        preferences = getContext().getSharedPreferences(PREFS_NAME_TOKEN, Context.MODE_PRIVATE);
        ip = preferences.getString("IP", "No IP");

        volleyMoviesRequest();
    }

    public void volleyMoviesRequest() {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = "https://" + ip + "/api/v1/films?offset=0&count=30";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                Movie movie = new Movie();
                                movie.setTitle(object.getString("title"));
                                movie.setDescription(object.getString("description"));
                                movie.setReleaseDate(object.getString("release_year"));
                                movieArrayList.add(movie);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        movieAdapter.notifyDataSetChanged();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "Something went wrong");
                    }
                }) {

            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences preferences = getContext().getSharedPreferences(PREFS_NAME_TOKEN, Context.MODE_PRIVATE);
                String token = preferences.getString("TOKEN", "No token");

                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("X-Access-Token", token);
                return headers;
            }

//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }

//            public byte[] getBody() throws AuthFailureError {
//                String mContent = "{}";
//                byte[] body = new byte[0];
//                try {
//                    body = mContent.getBytes("UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                return body;
//            }
        };

        requestQueue.add(jsonArrayRequest);
    }
}
