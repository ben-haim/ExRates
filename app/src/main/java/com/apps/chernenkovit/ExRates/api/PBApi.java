package com.apps.chernenkovit.ExRates.api;


import com.apps.chernenkovit.ExRates.model.CurrentRates;
import com.apps.chernenkovit.ExRates.model.DateRates;

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
