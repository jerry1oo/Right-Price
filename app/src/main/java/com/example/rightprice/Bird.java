package com.example.rightprice;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;


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

//Bird Vehicle factory
public class Bird {

    //bird vehicles
    private List<Vehicle>birds;
    //init request for bird
    private JsonObjectRequest initReq;

    public void setToken(String token) {
        this.token = token;
    }

    //token for validation
    private String token;


    private String id;
    private String email;
    private JsonObjectRequest vehicleReq;

    public void setId(String id) {
        this.id = id;
    }



    public JsonObjectRequest getInitReq() {
        return initReq;
    }


    public List<Vehicle> getVehicles() {
        return birds;
    }

    public JsonObjectRequest getVehicleReq() {
        return vehicleReq;
    }



    public String getId() {
        return id;
    }

    //create vehicles from request response
    public void generateVehicles(JSONObject resp) throws JSONException {
        birds = new ArrayList<Vehicle>();
        JSONArray items = resp.getJSONArray("birds");
        double lat = 0;
        double lng = 0;
        String id = "";
        int bat = 0;
        System.out.println(items.toString());
        System.out.println("parsing.....");
        for(int i=0;i<items.length();++i){
            JSONObject current = (JSONObject) items.get(i);
            //Vehicle(String vendor,String id, int battery, double lat, double lng, double startPrice, double minutePrice)
            //birds.add(new Vehicle("bird",items.get(i).getString("id");
            if(current.getString("captive").equals("false")) {
                JSONObject loc = current.getJSONObject("location");
                lat = loc.getDouble("latitude");
                lng = loc.getDouble("longitude");
                bat = Integer.parseInt(current.getString("battery_level"));
                id = current.getString("id");

                Vehicle veh = new Vehicle("bird",id,bat,lat,lng,"$1 to unlock $0.27 / 1 min");
                veh.setType("scooter");
                System.out.print(veh);

                birds.add(veh);
            }
        }
/*        for(int i=0;i<birds.size();++i){
            System.out.println(birds.get(i));
        }
        */

    }

    //set basic fields for bird request... email does not matter
    public Bird() {
        email = "johnathan@ucsd.com";
        id = "eee4913d-078e-4f13-8bd6-87d3245a3fb0";
        //token="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBVVRIIiwidXNlcl9pZCI6ImRiN2IwMGIzLWE2NWUtNDQyMy1iZDIzLWZlOGVkZTk3NWNmMyIsImRldmljZV9pZCI6IjQ3OTIwMzZkLWVkNGEtNDQ5OC05ZGJjLTViMjlmZjNmMWVmNSIsImV4cCI6MTU5MDgwMTkxMH0.hmhXizqW64omSvjdbhabdMcJBPECdzq2MVtObov2drs";
        token = "Bird eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBVVRIIiwidXNlcl9pZCI6ImRiN2IwMGIzLWE2NWUtNDQyMy1iZDIzLWZlOGVkZTk3NWNmMyIsImRldmljZV9pZCI6IjQ3OTIwMzZkLWVkNGEtNDQ5OC05ZGJjLTViMjlmZjNmMWVmNSIsImV4cCI6MTU5MDgwMTkxMH0.hmhXizqW64omSvjdbhabdMcJBPECdzq2MVtObov2drs";

    }


    //generate init request to renew token
    public void generateInitReq(Response.Listener<JSONObject> onRes) throws JSONException {
        JSONObject obj = new JSONObject();
        String url = "https://api.bird.co/user/login";
        obj.put("email",email);

        Response.ErrorListener onErr = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error Request when Init BIRD");
                //bad request or something

            }
        };
        initReq = new JsonObjectRequest(Request.Method.POST,url,obj,onRes,onErr) {
            /**
             * Passing some request headers*
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Device-id:", "000bca56-fb54-4704-9abe-60efc4d9993c");
                headers.put("Platform","android");
                return headers;
            }
        };

    }


   //generate the vehicle request for bird
    public void generateVehicleReq(final Location point, int radius, Response.Listener<JSONObject> onRes) throws JSONException {

        String url = "https://api.bird.co/bird/nearby?";

        url+="latitude="+point.getLatitude();
        url+="&longitude="+point.getLongitude();
        url+="&radius="+radius;

        JSONObject obj = new JSONObject();


        Response.ErrorListener onErr = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error " +
                        "on GET request from bird");
                System.out.println(error.toString());
                //bad request or something

            }
        };
        //set vehicle request
        vehicleReq = new JsonObjectRequest(Request.Method.GET,url,obj,onRes,onErr) {
            /**
             * add headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", token);
                headers.put("Device-id:", "801087ee-88a2-4ff7-9067-2e090007ffb6");
                headers.put("App-Version","3.0.5");

                String locH = "{";
                locH+= "\"latitude\":"+point.getLatitude()+",";
                locH+= "\"longitude\":"+point.getLongitude()+",";
                locH+= "\"altitude\":"+ 0 + ",";
                locH+= "\"accuracy\":"+ 100 + ",";
                locH+= "\"speed\":"+ -1 + ",";
                locH+= "\"heading\":"+ -1;
                locH+= "}";

                headers.put("Location",locH);
                return headers;
            }
        };
    }

}