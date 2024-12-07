package com.voila.customtransfer;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static final Map<String,String> byCode = new HashMap<>();
    public static final Map<String,String> byName = new HashMap<>();

    public static HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws Throwable {
        initStation();
//        getTrain(byName.get("中山北"), byName.get("广州南"), "2024-12-20").forEach(System.out::println);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new MainForm().setVisible(true);
    }

    static void initStation()throws Throwable{
        String body = client.send(HttpRequest.newBuilder()
            .uri(URI.create("https://kyfw.12306.cn/otn/resources/js/framework/station_name.js"))
            .build(), HttpResponse.BodyHandlers.ofString()).body();
        String raw=body.substring(body.indexOf('\'')+1,body.lastIndexOf('\''));
        for(String line : raw.split("@")){
            String[] split = line.split("\\|");
            if(split.length < 3){
                continue;
            }
            byCode.put(split[2],split[1]);
            byName.put(split[1],split[2]);
        }
    }

    public static List<TrainInfo> getTrain(String from, String to, String date){
        String raw = null;
        try{
            raw = client.send(HttpRequest.newBuilder()
                .uri(URI.create("https://kyfw.12306.cn/otn/leftTicket/queryO?leftTicketDTO.train_date=" + date + "&leftTicketDTO.from_station=" + from + "&leftTicketDTO.to_station=" + to + "&purpose_codes=ADULT"))
                .header("Cookie","JSESSIONID=861E392EDB55ED89377ABCADE480841B")
                .build(), HttpResponse.BodyHandlers.ofString()).body();
        }catch(IOException | InterruptedException e){
            throw new RuntimeException(e);
        }
//        System.out.println(raw);
        JSONObject body = new JSONObject(raw);
        JSONArray result=body.getJSONObject("data").getJSONArray("result");
        List<TrainInfo> ret = new ArrayList<>();
        for(int i = 0, len = result.length(); i < len; i++){
            String line = result.getString(i);
            String[] col = line.split("\\|");
            String startTimeStr=col[8];
            String endTimeStr=col[9];
            String[] timeSplit = startTimeStr.split(":");
            int startTime = Integer.parseInt(timeSplit[0]) * 60 + Integer.parseInt(timeSplit[1]);
            timeSplit=endTimeStr.split(":");
            int endTime = Integer.parseInt(timeSplit[0]) * 60 + Integer.parseInt(timeSplit[1]);
            int secondSeat = col[30].isEmpty() || col[30].equals("无") ? 0 : col[30].equals("有") ? 30 : Integer.parseInt(col[30]);
            int firstSeat = col[31].isEmpty() || col[31].equals("无") ? 0 : col[31].equals("有") ? 30 : Integer.parseInt(col[31]);
            int noSeat = col[26].isEmpty() || col[26].equals("无") ? 0 : col[26].equals("有") ? 30 : Integer.parseInt(col[26]);
            int hardSeat = col[29].isEmpty() || col[29].equals("无") ? 0 : col[29].equals("有") ? 30 : Integer.parseInt(col[29]);
            int hardSleeper = col[28].isEmpty() || col[28].equals("无") ? 0 : col[28].equals("有") ? 30 : Integer.parseInt(col[28]);
            if(secondSeat + firstSeat + noSeat + hardSeat + hardSleeper == 0){
                continue;
            }
            ret.add(new TrainInfo(col[6], col[7], col[3], startTime, endTime, startTimeStr, endTimeStr, col[10], secondSeat, firstSeat, noSeat, hardSeat, hardSleeper));
        }
        return ret;
//        System.out.println("train_no = " + col[2]);
//        System.out.println("station_train_code = " + col[3]);
//        System.out.println("start_station_telecode = " + col[4]);
//        System.out.println("end_station_telecode = " + col[5]);
//        System.out.println("from_station_telecode = " + col[6]);
//        System.out.println("to_station_telecode = " + col[7]);
//        System.out.println("start_time = " + col[8]);
//        System.out.println("arrive_time = " + col[9]);
//        System.out.println("lishi = " + col[10]);
//        System.out.println("canWebBuy = " + col[11]);
//        System.out.println("yp_info = " + col[12]);
//        System.out.println("start_train_date = " + col[13]);
//        System.out.println("train_seat_feature = " + col[14]);
//        System.out.println("location_code = " + col[15]);
//        System.out.println("from_station_no = " + col[16]);
//        System.out.println("to_station_no = " + col[17]);
//        System.out.println("is_support_card = " + col[18]);
//        System.out.println("controlled_train_flag = " + col[19]);
//        System.out.println("gg_num = " + (!col[20].isEmpty() ? col[20] : "--"));
//        System.out.println("gr_num = " + (!col[21].isEmpty() ? col[21] : "--"));
//        System.out.println("qt_num = " + (!col[22].isEmpty() ? col[22] : "--"));
//        System.out.println("rw_num = " + (!col[23].isEmpty() ? col[23] : "--"));
//        System.out.println("rz_num = " + (!col[24].isEmpty() ? col[24] : "--"));
//        System.out.println("tz_num = " + (!col[25].isEmpty() ? col[25] : "--"));
//        System.out.println("wz_num = " + (!col[26].isEmpty() ? col[26] : "--"));
//        System.out.println("yb_num = " + (!col[27].isEmpty() ? col[27] : "--"));
//        System.out.println("yw_num = " + (!col[28].isEmpty() ? col[28] : "--"));
//        System.out.println("yz_num = " + (!col[29].isEmpty() ? col[29] : "--"));
//        System.out.println("ze_num = " + (!col[30].isEmpty() ? col[30] : "--"));
//        System.out.println("zy_num = " + (!col[31].isEmpty() ? col[31] : "--"));
//        System.out.println("swz_num = " + (!col[32].isEmpty() ? col[32] : "--"));
//        System.out.println("srrb_num = " + (!col[33].isEmpty() ? col[33] : "--"));
//        System.out.println("yp_ex = " + col[34]);
//        System.out.println("seat_types = " + col[35]);
//        System.out.println("exchange_train_flag = " + col[36]);
//        System.out.println("houbu_train_flag = " + col[37]);
//        System.out.println("houbu_seat_limit = " + col[38]);
//        System.out.println("yp_info_new = " + col[39]);
//        System.out.println("dw_flag = " + col[46]);
//        System.out.println("stopcheckTime = " + col[48]);
//        System.out.println("country_flag = " + col[49]);
//        System.out.println("local_arrive_time = " + col[50]);
//        System.out.println("local_start_time = " + col[51]);
//        System.out.println("bed_level_info = " + col[53]);
//        System.out.println("seat_discount_info = " + col[54]);
//        System.out.println("sale_time = " + col[55]);
    }

    public static List<List<TrainInfo>> getAlongTickets(Station[] stations, String date){
        List<List<TrainInfo>> ret=new ArrayList<>();
        if(stations.length < 2)return ret;
        for(int i = 0, stationsLength = stations.length; i < stationsLength-1; i++){
            Station start = stations[i];
            Station end = stations[i + 1];
            ret.add(getTrain(start.id(), end.id(), date));
        }
        return ret;
    }
}
