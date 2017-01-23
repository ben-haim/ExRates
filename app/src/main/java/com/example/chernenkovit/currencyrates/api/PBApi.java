package com.example.chernenkovit.currencyrates.api;


import com.example.chernenkovit.currencyrates.model.CurrentRates;
import com.example.chernenkovit.currencyrates.model.DateRates;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/** RESTful services description. */
public interface PBApi {

    @GET("p24/accountorder?oper=prp&PUREXML&apicour&country&full")
    Call<CurrentRates> getCurrentRates(@Query("country") String country);

    @GET("p24api/exchange_rates?json")
    Call<DateRates> getDateRates(@Query("date") String date);

 }
