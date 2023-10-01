package com.example.fakebnb.rest;

import com.example.fakebnb.model.SearchRentalModel;
import com.example.fakebnb.model.response.ApartmentPagedResponse;
import com.example.fakebnb.model.response.ApartmentResponse;
import com.example.fakebnb.model.response.RecommendationResponse;
import com.example.fakebnb.model.response.UserRegResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApartmentAPI {

    @Multipart
    @POST("/app/apartment")
    Call<ApartmentResponse> createApartment(@Query(value = "apartmentRequest") String apartmentRequest,
                                            @Part List<MultipartBody.Part> image);

    @Multipart
    @PUT("/app/apartment/image/{apartmentId}")
    Call<ApartmentResponse> updateApartmentWithImage(@Path("apartmentId") Long apartmentId,
                                                     @Query(value = "apartmentRequest") String apartmentRequest,
                                                     @Part List<MultipartBody.Part> image);

    @PUT("/app/apartment/{apartmentId}")
    Call<ApartmentResponse> updateApartment(@Path("apartmentId") Long apartmentId,
                                            @Query(value = "apartmentRequest") String apartmentRequest);

    @GET("/app/apartment/{apartmentId}")
    Call<ApartmentResponse> getApartmentInfo(@Path("apartmentId") Long apartmentId);

    @DELETE("/app/apartment/{apartmentId}")
    Call<ApartmentResponse> deleteApartment(@Path("apartmentId") Long apartmentId);

    @GET("/app/apartment/{apartmentId}/host")
    Call<UserRegResponse> getHostId(@Path("apartmentId") Long apartmentId);

    @GET("/app/apartment/{hostId}")
    Call<ApartmentPagedResponse> getHostApartments(@Path(value = "hostId") Long hostId,
                                                   @Query("page") int page,
                                                   @Query("size") int size);

    @GET("/app/apartment/rec/{userId}")
    Call<RecommendationResponse> getRecommendedApartments(@Path(value = "userId") Long userId);
}