package com.e.easearch.controller;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;


public class GsonRequest<T> extends JsonRequest<T> {

    private final Gson mGson;
    private final Type mType;



    public GsonRequest(int method, String url, JSONObject requestObject, Type type, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, (requestObject == null) ? null : requestObject.toString(), listener, errorListener);
        mType = type;
        mGson = new Gson();
    }



    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            T parseObject = mGson.fromJson(jsonString, mType);  // Aca parsea la lista al tipo T
            return Response.success(parseObject, HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

}

