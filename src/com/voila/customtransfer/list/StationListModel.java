package com.voila.customtransfer.list;

import com.voila.customtransfer.Station;

import javax.swing.*;
import java.util.Enumeration;

public class StationListModel extends DefaultListModel<Station> {
    @Override
    public void addElement(Station element){
        if(contains(element)) return;
        super.addElement(element);
    }

    public Station[] getElements(){
        Enumeration<Station> elements = elements();
        Station[] stations = new Station[getSize()];
        int i = 0;
        while(elements.hasMoreElements()){
            Station element = elements.nextElement();
            stations[i++] = element;
        }
        return stations;
    }
}
