package com.googleapi.scpandroiddata;




import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DataService {
    private static final String BASE_URL = "http://www.newdigilabs.com/scp/";
    private final OkHttpClient client;
    private final Gson gson;

    public DataService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public ApiResponse getData(String searchValue, int page, int pageSize) throws IOException {
        String url = BASE_URL + "api.php";

        RequestData requestData = new RequestData(searchValue, page, pageSize);
        String json = gson.toJson(requestData);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseString = response.body().string();
            return gson.fromJson(responseString, ApiResponse.class);
        }
    }

    public InputStream exportToExcel(String deviceId) throws IOException {
        String url = BASE_URL + "export.php";

        RequestData requestData = new RequestData(deviceId, 0, 0);
        String json = gson.toJson(requestData);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return response.body().byteStream();
    }

    private static class RequestData {
        private String device_id;
        private int page;
        private int page_size;

        public RequestData(String device_id, int page, int page_size) {
            this.device_id = device_id;
            this.page = page;
            this.page_size = page_size;
        }
    }
}