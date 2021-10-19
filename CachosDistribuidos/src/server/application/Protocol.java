/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.application;

import java.util.ArrayList;
import org.json.*;

/**
 *
 * @author Paul
 */
public class Protocol {

    private int code;
    private ArrayList<String> data;
    private JSONObject jsonObject;
    private String message;
    private String content;

    public Protocol(String content) {
        this.content = content;
        data = new ArrayList<>();
        jsonObject = new JSONObject(content);
        message = "";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public int getCode() {
        return code;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setContent(String content) {
        this.content = content;
        jsonObject = new JSONObject(content);
    }

    public void process() {
        setCode(jsonObject.getInt("code"));
        setMessage(jsonObject.getString("message"));
        JSONArray dataArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            data.add(dataArray.getString(i));
        }

    }

}
