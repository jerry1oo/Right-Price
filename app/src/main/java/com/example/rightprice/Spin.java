package com.example.rightprice;
import android.location.Location;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//Factory for Spin vehicles
public class Spin {
    public List<Vehicle> getVehicles() {
        return spins;
    }

    //spin vehicles
    private List<Vehicle> spins;
    //vehicle request for spin
    private JsonObjectRequest vehicleReq;
    //init request for spin
    private JsonObjectRequest initReq;
    //token for spin
    private String token;

    //generate the init request for spin..sets token
    public void generateInitReq(Response.Listener<JSONObject> onRes) throws JSONException {
        String url ="https://web.spin.pm/api/v1/auth_tokens";
        JSONObject obj = new JSONObject();
        JSONObject innerObj = new JSONObject();
        innerObj.put("mobileType","andorid");
        innerObj.put("uid","3fbdb6d9-199f-4038-9c10-b9f85228ac9a");
        obj.put("device",innerObj);
        obj.put("grantType","device");

        //listener if the request fails
        Response.ErrorListener onErr = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error Request when INIT  SPIN");
                //bad request or something

            }
        };

        //init request set
        initReq = new JsonObjectRequest(Request.Method.POST,url,obj,onRes,onErr) {
            /**
             * add headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
    }


    //generate the vehicles from the response from spin
    public void generateVehicles(JSONObject response) throws JSONException {
        System.out.println("I got the spins ...");
        String id;
        String type;
        String rate;
        double lat;
        double lng;
        int bat;
        spins = new ArrayList<Vehicle>();
        JSONArray items = response.getJSONArray("vehicles");
        for(int i=0;i<items.length();++i){
            JSONObject current = (JSONObject) items.get(i);
            lat = current.getDouble("lat");
            lng = current.getDouble("lng");
            type = current.getString("vehicle_type");
            id = current.getString("last4");
            if(type.equals("scooter")) {
                bat = current.getInt("batt_percentage");
                rate = "$1 to unlock $0.15 / 1 min";
            }
            else{
                bat = -1;
                rate = "$1 for 30 mins";
            }
            Vehicle veh = new Vehicle("spin",id,bat,lat,lng,rate);
            veh.setType(type);
            spins.add(veh);
        }

        for(int i=0;i<spins.size();++i){
            System.out.println(spins.get(i));
        }
    }

    //generate the vehicle requests for spin
    public void generateVehicleReq(final Location loc,Response.Listener<JSONObject> onRes) {
        String url = "https://web.spin.pm/api/v3/vehicles?";
        url += "lng=" + loc.getLongitude();
        url += "&lat=" +loc.getLatitude();
        url += "&distance=&mode=";

        JSONObject obj = new JSONObject();

        //response listner in case spin request goes bad
        Response.ErrorListener onErr = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error " +
                        "on GET request from SPIN");
                System.out.println(error.toString());
                //bad request or something

            }
        };


        //set vehicle request
        vehicleReq = new JsonObjectRequest(Request.Method.GET, url, obj, onRes, onErr){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", token);
                return headers;
            }
        };
    }


    //empty constructor.. the requests set all fields  for this factory
    public Spin(){


    }

    public JsonObjectRequest getVehicleReq() {
        return vehicleReq;
    }
    public void setToken(String token){
        this.token = token;
    }

    public JsonObjectRequest getInitReq() {
        return initReq;
    }
}
