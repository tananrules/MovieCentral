/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cmpe275.service;

import com.cmpe275.entity.Movie;
import com.cmpe275.entity.PlaybackHistory;
import com.cmpe275.repository.PlaybackHistoryRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 *
 * @author Shreya Shah
 */
@Service
public class PlaybackHistoryService {

    @Autowired
    private PlaybackHistoryRepository playbackHistoryRepository;

    public PlaybackHistory addPlaybackHistory(PlaybackHistory playbackHistory) {

        PlaybackHistory playbackHistoryObj = null;
        Calendar currentCal = Calendar.getInstance();
        Date currentDate = currentCal.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.HOUR, -24);
        Date previousDate = cal.getTime();
        List<PlaybackHistory> playbackHistorys = playbackHistoryRepository.getAllPlayBackHistoryByUserByTime(previousDate, currentDate, playbackHistory.getUserObj().getUserId(),playbackHistory.getMovieObj().getMovieId());
        if (CollectionUtils.isEmpty(playbackHistorys)) {
            playbackHistoryObj = playbackHistoryRepository.save(playbackHistory);
        }
        return playbackHistoryObj;
    }

    public List<Movie> getMostWatchedMovies(){
        return playbackHistoryRepository.getMostWatchedMovies(PageRequest.of(0,10)).getContent();
    }

}