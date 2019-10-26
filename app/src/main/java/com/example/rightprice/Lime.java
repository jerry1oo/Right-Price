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

//Factory for the Lime vehicles
public class Lime {

    //vehicles from lime service
    private List<Vehicle> limes;
    //initialization Requet
    private JsonObjectRequest initReq;


    //vehicle request
    private JsonObjectRequest vehicleReq;
    private String token;
    private String phoneNumber;


    public List<Vehicle> getVehicles() {
        return limes;
    }

    public JsonObjectRequest getVehicleReq() {
        return vehicleReq;
    }



    //standard constructor sets token
    public Lime(){
        token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX3Rva2VuIjoiM1dTWjdHVEFRR1c3MiIsImxvZ2luX2NvdW50Ijo4fQ.za5fKOSbwyTzrp4_8d6H1T7rijeVfpO-zTXxEXFA3H0";
    }

    //generate the vehicle request for lime
    public void generateVehicleReq(final Location loc, int zoom /*not sure what this is*/,Response.Listener<JSONObject> onRes) {
        //Example point..
        //loc = 32.880457,-117.237612
        //NE-> = 32.888998, -117.225209
        //SW-> = 32.870545, -117.243148

        double user_latitude = loc.getLatitude();
        double user_longitude = loc.getLongitude();
        double ne_lat = user_latitude + .01;
        double ne_lng = user_longitude + .013;
        double sw_lat = user_latitude - .01;
        double sw_lng = user_longitude - .013;


        //SAMPLE:
        //https://web-production.lime.bike/api/rider/v1/views/map
        String url = "https://web-production.lime.bike/api/rider/v1/views/map?";

        //?authorization=Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX3Rva2VuIjoiM1dTWjdHVEFRR1c3MiIsImxvZ2luX2NvdW50Ijo4fQ.za5fKOSbwyTzrp4_8d6H1T7rijeVfpO-zTXxEXFA3H0
        url += "authorization=" + token;
        url += "&ne_lat=" + ne_lat + "&ne_lng=" + ne_lng + "&sw_lat=" + sw_lat + "&sw_lng=" + sw_lng;

        url += "&user_latitude=" + user_latitude + "&user_longitude=" + user_longitude;
        url += "&zoom=" + zoom;
        JSONObject obj = new JSONObject();


        //simple error request listener
        Response.ErrorListener onErr = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error Request when getting Limes");
                //bad request or something

            }
        };
        //sets vehicle request
        vehicleReq = new JsonObjectRequest(Request.Method.GET, url, obj, onRes, onErr) {
            /**
             * set headers..
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Cookie", "_limebike-web_session=K0ZVQkpibTd5TUowbFYrV2xVOXZBSHN5dEZKcTBEVjU1RUcxdlY1djZJTnlDWXdYMlRkMkN5SDl0am5sTHVxMmZxblhBUmN6cEFJSExHTFh5dVVBK3llVTY3OEtuZmRSY0l4RVptdGxLa3BQTFFKQUdmMXhuSTVOWkwrWUtOcmVWcUJUOGFISU55dkxMeGp3WjZ6UFU2S1ZVb3hGSU1JY0g3VXFPdzh1N0Z6KzFuamJBZjlZTHQ3ZmVtTXVWUmVnN2poeEpPLy9PRU9kaTlvVUVqdnhKZmhacUFmSmJwZmY4YUNnMFpWZ2RIN2RTckppbE1yV0tXOW5ScHBULzlzWStJUGJZMUQwTmpVSXNKdmlOci80bTRlZkltRTUyUVZaUk9WVTliTm14QXNCK2tnV21ERXJPYTd5cE5EK0NmRThqaWRMREYzakQwdEl0WFBqOXNZWlNBQk1iMENhUWJ2SkdhRjlhU2JBV2RmRUZGTzJqS3ZPMlR3bHVwZWRnSFZZZnBnd1paZSttbzM5dVNrQU4rVkR2NWlITW9kZTVQVEVZS01EMlJQbU9yVT0tLXRXTG1ZVHdlYnV4TG95cElLM2xIRlE9PQ%3D%3D--6cc3f3b3e98103c4c4a9d8a1ff2259969cb02996; path=/; domain=.web-production.lime.bike; Secure; HttpOnly; Expires=Tue, 19 Jan 2038 03:14:07 GMT;");
                return headers;
            }
        };

    }

    //generate the vehicles from the response recieved from lime
    public void generateVehicles(JSONObject response) throws JSONException {
        limes = new ArrayList<Vehicle>();
        JSONArray items = response.getJSONObject("data").getJSONObject("attributes").getJSONArray("bikes");
        String id = "";
        String rate;
        String type;
        String temp_bat;
        double lat;
        double lng;
        int bat;


        for (int i = 0; i < items.length(); ++i) {
            JSONObject current = (JSONObject) items.get(i);
            id = current.getString("id");
            JSONObject attributes = current.getJSONObject("attributes");
            lat = attributes.getDouble("latitude");
            lng = attributes.getDouble("longitude");
            temp_bat = attributes.getString("battery_level");
            switch (temp_bat) {
                case "high":
                    bat = 85;
                    break;
                case "medium":
                    bat = 50;
                    break;
                case "low":
                    bat = 25;
                    break;
                default:
                    bat = 0;
            }

            rate = attributes.getString("rate_plan");
            rate = rate.replace("\n", "");
            rate = rate.replace("+", "");
            type = attributes.getString("type_name");


            Vehicle veh = new Vehicle("lime", id, bat, lat, lng, rate);
            veh.setType(type);
            limes.add(veh);


            System.out.println("Here are the limes.....");

        }

        for (int i = 0; i < limes.size(); ++i) {
            System.out.println(limes.get(i));
        }

    }
}