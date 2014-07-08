package com.example.fibe.studentfibeclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * Created by Victoria on 2014-07-07.
 */
public class JSOnCreation {

    public String discovery (){

        String methods = "";
        try {

            Class c = Class.forName("JSOnCreation");
            Method[] method = c.getDeclaredMethods();

            for (int i = 0; i < method.length; i++) {
                methods += method[i].toString();
                methods += ", ";

            }
        }
        catch (Throwable e) {
            System.err.println(e);
            return "error";
        }
        return methods;
    }

    // ************************ JSON CREATION ************************

    /**
     * @author      Vicky Bukta
     * @return      JSONObject
     *
     * Encapsulate regist Json
     */
    public JSONObject create_regist (JSONObject object){

        try {
            JSONObject payload = new JSONObject();
            payload.put("username", "");
            payload.put("password", "");
            object.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * @author      Vicky Bukta
     * @return      JSONObject
     *
     * Encapsulate login Json
     */
    public JSONObject create_login (JSONObject object){

        try {
            JSONObject payload = new JSONObject();
            payload.put("username", "");
            payload.put("password", "");
            payload.put("sessionkey", "");
            object.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * @author      Vicky Bukta
     * @return      JSONObject
     *
     * Encapsulate audio enqueue Json
     */
    public JSONObject create_enqueueJSON (JSONObject object){

        try {
            JSONArray path = new JSONArray();
            JSONObject payload = new JSONObject();
            payload.put("tags", path);
            payload.put("time", "");
            object.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * @author      Vicky Bukta
     * @return      JSONObject
     *
     * Encapsulate create group Json
     */
    public JSONObject create_group (JSONObject object){

        try {
            JSONObject payload = new JSONObject();
            payload.put("name", "");
            object.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * @author      Vicky Bukta
     * @return      JSONObject
     *
     * Encapsulate audio Json
     */
    public JSONObject audioJSON (JSONObject object){

        try {
            JSONArray path = new JSONArray();
            JSONObject payload = new JSONObject();
            payload.put("name", "");
            object.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * @author      Vicky Bukta
     * @return      JSONObject
     *
     * Encapsulate audioCancel Json
     */
    public JSONObject audioCancel (JSONObject object){

        try {
            JSONObject payload = new JSONObject();
            object.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * @author      Vicky Bukta
     * @return      JSONObject
     *
     * Encapsulate audioPermit Json
     */
    public JSONObject audioPermit (JSONObject object){

        try {
            JSONObject payload = new JSONObject();
            payload.put("target-user", "");
            object.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * @author      Vicky Bukta
     * @return      JSONObject
     *
     * Encapsulate Json
     */

    public JSONObject genericJSON (){

        JSONObject object = new JSONObject();
        try {
            JSONArray path = new JSONArray();
            object.put("sessionid", 0);
            object.put("path", path);
            object.put("request", "create_audio_queue");
            object.put("identity", 0);
            object.put("sessionkey", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }
}
