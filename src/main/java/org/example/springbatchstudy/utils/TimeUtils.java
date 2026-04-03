package org.example.springbatchstudy.utils;

import org.springframework.stereotype.Component;

@Component
public class TimeUtils {
    public String convertDuration(long durationInMillis) {

        final long hours = durationInMillis/(1000* 60 * 60);
        final long minutes = (durationInMillis % (1000 * 60 * 60)) / (1000 * 60);
        final long seconds = (durationInMillis % (1000 * 60)) / 1000;

        String duration ="";
        if(hours >0){
            duration = String.format("%d시간 %d분 %d초", hours, minutes, seconds);
        } else if (minutes >0) {
            duration = String.format("%d분 %d초", minutes, seconds);
        } else {
            duration = String.format("%d초", seconds);
        }
        return duration;

    }
}
