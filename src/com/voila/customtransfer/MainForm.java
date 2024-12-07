/*
 * Created by JFormDesigner on Fri Dec 06 19:13:30 CST 2024
 */

package com.voila.customtransfer;

import java.awt.event.*;

import com.voila.customtransfer.list.StationListModel;
import com.voila.customtransfer.list.TicketList;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * @author voila
 */
public class MainForm extends JFrame {
    private final StationListModel stationModel = new StationListModel();
    List<JScrollPane> ticketPanels = new ArrayList<>();
    List<JCheckBox> checkBoxes = new ArrayList<>();
    List<List<TrainInfo>> allRoutes;

    public MainForm(){
        initComponents();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        stationList.setModel(stationModel);
        stationList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
                return super.getListCellRendererComponent(list, ((Station) value).name(), index, isSelected, cellHasFocus);
            }
        });
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()));
    }

    private void addStationBtnClick(){
        String name = stationField.getText();
        String id = Main.byName.get(name);
        if(id == null) return;
        stationModel.addElement(new Station(id, name));
        stationField.setText("");
    }

    private List<List<TrainInfo>> applyFilter(){
        Set<String> selected = new HashSet<>();
        for(JCheckBox checkBox : checkBoxes){
            if(checkBox.isSelected()){
                selected.add(Main.byName.get(checkBox.getText()));
            }
        }

        List<List<TrainInfo>> copy = new ArrayList<>();
        for(List<TrainInfo> trains : allRoutes){
            ArrayList<TrainInfo> trainsCopy = new ArrayList<>(trains);
            trainsCopy.removeIf(info -> !selected.contains(info.from()) || !selected.contains(info.to()));
            copy.add(trainsCopy);
        }
        return copy;
    }

    private void fetchBtnClick(){
        if(stationModel.size() < 2) return;
        for(JScrollPane ticketPanel : ticketPanels){
            getContentPane().remove(ticketPanel);
        }
        for(JCheckBox checkBox : checkBoxes){
            getContentPane().remove(checkBox);
        }
        checkBoxes.clear();
        ticketPanels.clear();

        allRoutes = Main.getAlongTickets(stationModel.getElements(), dateField.getText());
        List<List<TrainInfo>> route = applyFilter();
        for(int i = 0, routeSize = route.size(); i < routeSize; i++){
            List<TrainInfo> trainInfos = route.get(i);
            JScrollPane scrollPane = new JScrollPane();
            TicketList list = getTrainInfoJList(trainInfos, ticketPanels.size());
            scrollPane.setViewportView(list);
            scrollPane.setBounds(200 * ticketPanels.size(), 180, 200, 275);
            getContentPane().add(scrollPane);

            Set<String> startStations = new HashSet<>();
            for(TrainInfo info : trainInfos){
                startStations.add(info.from());
            }

            int cbc=0;
            for(String startStation : startStations){
                JCheckBox checkBox = getFilterCheckBox(startStation, 200 * ticketPanels.size(), 455 + 20 * cbc);
                getContentPane().add(checkBox);
                checkBoxes.add(checkBox);
                cbc++;
            }

            if(i == routeSize - 1){
                cbc = 0;
                Set<String> endStations = new HashSet<>();
                for(TrainInfo info : trainInfos){
                    endStations.add(info.to());
                }
                for(String endStation : endStations){
                    JCheckBox checkBox = getFilterCheckBox(endStation, 200 * ticketPanels.size() + 100, 455 + 20 * cbc);
                    getContentPane().add(checkBox);
                    checkBoxes.add(checkBox);
                    cbc++;
                }

            }
            ticketPanels.add(scrollPane);
        }
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private JCheckBox getFilterCheckBox(String station,int x,int y){
        JCheckBox checkBox = new JCheckBox(Main.byCode.get(station));
        checkBox.setBounds(x,y,100,20);
        checkBox.addActionListener(e -> {

        });
        return checkBox;
    }

    private TicketList getTrainInfoJList(List<TrainInfo> trainInfos, int index){
        TicketList list = new TicketList(trainInfos, index);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e){
                route(list.index);
            }
        });
        return list;
    }

    private void route(int listIndex){
        for(int i = 0, ticketPanelsSize = ticketPanels.size(); i < ticketPanelsSize; i++){
            if(i == listIndex) continue;
            JScrollPane ticketPanel = ticketPanels.get(i);
            TicketList list = (TicketList) ((Container) ticketPanel.getComponent(0)).getComponent(0);
            list.clearSelection();
        }

        for(int i = listIndex; i < ticketPanels.size() - 1; i++){ // 右侧
            TicketList center = (TicketList) ((Container) ticketPanels.get(i).getComponent(0)).getComponent(0);
            TicketList right = (TicketList) ((Container) ticketPanels.get(i + 1).getComponent(0)).getComponent(0);
            TrainInfo centerInfo = center.getModel().getElementAt(center.getSelectedIndex());
            TrainInfo[] rightElem = enumToArray(((DefaultListModel<TrainInfo>) right.getModel()).elements(), new TrainInfo[0]);
            int suitable = findSuitable(centerInfo, rightElem, 1);
            if(suitable == -1) break;
            right.setSelectedIndex(suitable);
            right.ensureIndexIsVisible(suitable);
        }
        for(int i = listIndex; i >= 1; i--){ // 左侧
            TicketList center = (TicketList) ((Container) ticketPanels.get(i).getComponent(0)).getComponent(0);
            TicketList left = (TicketList) ((Container) ticketPanels.get(i - 1).getComponent(0)).getComponent(0);
            TrainInfo centerInfo = center.getModel().getElementAt(center.getSelectedIndex());
            TrainInfo[] leftElem = enumToArray(((DefaultListModel<TrainInfo>) left.getModel()).elements(), new TrainInfo[0]);
            int suitable = findSuitable(centerInfo, leftElem, 0);
            if(suitable == -1) break;
            left.setSelectedIndex(suitable);
            left.ensureIndexIsVisible(suitable);
        }

    }

    private int findSuitable(TrainInfo center, TrainInfo[] another, int direction){
        if(direction == 0){ // left
            for(int i = another.length - 1; i >= 0; i--){
                TrainInfo trainInfo = another[i];
                if(center.startTime() - trainInfo.endTime() >= 19){
                    return i;
                }
            }
        }else{
            for(int i = 0; i < another.length; i++){
                TrainInfo trainInfo = another[i];
                if(trainInfo.startTime() - center.endTime() >= 19){
                    return i;
                }
            }
        }
        return -1;
    }

    private static <T> T[] enumToArray(Enumeration<T> enumeration, T[] array){
        List<T> list = new ArrayList<>();
        while(enumeration.hasMoreElements()){
            list.add(enumeration.nextElement());
        }
        return list.toArray(array);
    }


    private void stationListMouseReleased(MouseEvent e){
        if(e.getButton() != 3 || stationList.getSelectedIndex() == -1) return;
        stationModel.remove(stationList.getSelectedIndex());
    }

    private void initComponents(){
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - john
        stationField = new JTextField();
        addStationBtn = new JButton();
        scrollPane1 = new JScrollPane();
        stationList = new JList();
        fetchBtn = new JButton();
        dateField = new JTextField();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(null);
        contentPane.add(stationField);
        stationField.setBounds(5, 5, 140, stationField.getPreferredSize().height);

        //---- addStationBtn ----
        addStationBtn.setText("text");
        addStationBtn.addActionListener(e -> addStationBtnClick());
        contentPane.add(addStationBtn);
        addStationBtn.setBounds(new Rectangle(new Point(145, 5), addStationBtn.getPreferredSize()));

        //======== scrollPane1 ========
        {

            //---- stationList ----
            stationList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    stationListMouseReleased(e);
                }
            });
            scrollPane1.setViewportView(stationList);
        }
        contentPane.add(scrollPane1);
        scrollPane1.setBounds(10, 45, 180, 125);

        //---- fetchBtn ----
        fetchBtn.setText("text");
        fetchBtn.addActionListener(e -> fetchBtnClick());
        contentPane.add(fetchBtn);
        fetchBtn.setBounds(new Rectangle(new Point(200, 140), fetchBtn.getPreferredSize()));
        contentPane.add(dateField);
        dateField.setBounds(200, 105, 170, dateField.getPreferredSize().height);

        contentPane.setPreferredSize(new Dimension(1005, 670));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - john
    private JTextField stationField;
    private JButton addStationBtn;
    private JScrollPane scrollPane1;
    private JList stationList;
    private JButton fetchBtn;
    private JTextField dateField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
