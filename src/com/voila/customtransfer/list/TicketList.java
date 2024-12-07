package com.voila.customtransfer.list;

import com.voila.customtransfer.TrainInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TicketList extends JList<TrainInfo> {
    public final int index;
    public TicketList(List<TrainInfo> data,int index) {
        setModel(new DefaultListModel<>() {
            {
                addAll(data);
            }
        });
        setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                TrainInfo trainInfo = (TrainInfo) value;
//                String v = String.format("%8s%s  %s", trainInfo.trainNumber(), trainInfo.startTimeStr(), trainInfo.endTimeStr());
                String v = trainInfo.trainNumber() + " ".repeat(8 - trainInfo.trainNumber().length()) + trainInfo.startTimeStr() + " " + trainInfo.endTimeStr();
                return super.getListCellRendererComponent(list, v, index, isSelected, cellHasFocus);
            }
        });
        setFont(new Font("Consolas",Font.PLAIN,16));
        this.index = index;
    }
}
