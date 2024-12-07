package com.voila.customtransfer;

public record TrainInfo(
    String from,
    String to,
    String trainNumber,
    int startTime,
    int endTime,
    String startTimeStr,
    String endTimeStr,
    String duration,
    int secondSeat,
    int firstSeat,
    int noSeat,
    int hardSeat,
    int hardSleeper
) {
    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder("TrainInfo{");
        sb.append("from='").append(from).append('\'');
        sb.append(", to='").append(to).append('\'');
        sb.append(", trainNumber='").append(trainNumber).append('\'');
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", startTimeStr='").append(startTimeStr).append('\'');
        sb.append(", endTimeStr='").append(endTimeStr).append('\'');
        sb.append(", duration='").append(duration).append('\'');
        sb.append(", secondSeat=").append(secondSeat);
        sb.append(", firstSeat=").append(firstSeat);
        sb.append(", noSeat=").append(noSeat);
        sb.append(", hardSeat=").append(hardSeat);
        sb.append(", hardSleeper=").append(hardSleeper);
        sb.append('}');
        return sb.toString();
    }
}
