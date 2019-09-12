package com.itsp.attendance;

import android.util.Log;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MessageWebSocket  extends WebSocketListener
{
    private final static String TAG = "MessageWebSocket";
    public void run() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0,  TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(Config.urlSocket)
                .build();
        client.newWebSocket(request, this);

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
    }

    @Override public void onOpen(WebSocket webSocket, Response response) {

        JSONObject payload = new JSONObject();
        try {
            payload.put("auth", Config.accessToken);
            payload.put("type", "auth");

            try {
                Algorithm algorithm = Algorithm.HMAC256("itsp300");
                String token = JWT.create().withClaim("asdasd", "aqwerqwer")
                        .sign(algorithm);
                webSocket.send(token);

                Log.d(TAG, "onOpen: " + "Socket Token: " + token);
            } catch (JWTCreationException exception){
                //Invalid Signing configuration / Couldn't convert Claims.
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override public void onMessage(WebSocket webSocket, String text) {
        System.out.println("MESSAGE: " + text);
    }

    @Override public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("MESSAGE: " + bytes.hex());
    }

    @Override public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        System.out.println("CLOSE: " + code + " " + reason);
    }

    @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }
}

