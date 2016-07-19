package com.bily.samuel.spseinstructor.lib.fbm;

import android.os.AsyncTask;
import android.util.Log;

import com.bily.samuel.spseinstructor.lib.JSONParser;
import com.bily.samuel.spseinstructor.lib.database.DatabaseHelper;
import com.bily.samuel.spseinstructor.lib.database.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by samuel on 14.7.2016.
 */
public class FbInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FbInstanceIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        final String regId = token;
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                DatabaseHelper db;
                db = new DatabaseHelper(getApplicationContext());
                User user = db.getUser();
                JSONParser jsonParser = new JSONParser();
                HashMap<String, String> values = new HashMap<>();
                values.put("tag", "setRegId");
                values.put("regId",regId);
                values.put("id_i","" + user.getIdu());
                try{
                    Log.e("sending",values.toString());
                    JSONObject jsonObject = jsonParser.makePostCall(values);
                    //Log.e("getting",jsonObject.toString());
                    if (jsonObject.getInt("success") == 1) {
                    }else{
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
