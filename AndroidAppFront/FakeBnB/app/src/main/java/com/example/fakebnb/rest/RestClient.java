package com.example.fakebnb.rest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class RestClient {

    private final String BASE_URL = "https://192.168.1.6:8443/";
    private Retrofit retrofit = null;
    private String authToken = null;

    public RestClient () {
        initializeRetrofit();
    }

    public RestClient(String authToken) {
        this.authToken = authToken;
        initializeRetrofit();
    }

    private void initializeRetrofit() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // Enable TLS support
        try {
            // Create a custom TrustManager that trusts all certificates
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);

            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new javax.net.ssl.TrustManager[]{trustManager}, null);
            httpClient.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
            httpClient.hostnameVerifier((hostname, session) -> true); // Bypass hostname verification (for testing/not for production

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (authToken != null) {
            httpClient.addInterceptor(new AuthInterceptor(authToken));
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getClient() {
        return retrofit;
    }

}
