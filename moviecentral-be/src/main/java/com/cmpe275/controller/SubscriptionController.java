package com.cmpe275.controller;

import javax.servlet.http.HttpSession;

import com.cmpe275.entity.Movie;
import com.cmpe275.entity.User;
import com.cmpe275.service.MovieServ;
import com.cmpe275.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.cmpe275.entity.UserSubscription;
import com.cmpe275.service.SubscriptionService;
import com.cmpe275.utility.ResponseFormat;
import java.util.List;
import org.springframework.util.CollectionUtils;

@Controller
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserService userServ;
    private final HttpSession session;
    private final MovieServ movieServ;

    ResponseFormat responseObject = new ResponseFormat();

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService, UserService us, HttpSession s, MovieServ ms) {
        this.subscriptionService = subscriptionService;
        this.userServ = us;
        this.session = s;
        this.movieServ = ms;
    }

    @PostMapping(path = "/payment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSubscription(@RequestBody UserSubscription us, HttpSession session) {
        try {
            UserSubscription result = subscriptionService.addSubscription(us);
            if (result != null) {
                responseObject.setData(result);
                responseObject.setMeta("Subscription created succesfully");
                return new ResponseEntity(responseObject, HttpStatus.OK);
            } else {
                responseObject.setData(null);
                responseObject.setMeta("Subscription already exists");
                return new ResponseEntity(responseObject, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseObject.setData(e);
            return new ResponseEntity(responseObject, HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping(path = "/income/subscribed/{month}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMonthlySubscriptionIncome(@PathVariable("month") Integer month) {
        Long monthlySubscriptionIncome = subscriptionService.getMonthlySubscriptionIncome(month);
        responseObject.setData(monthlySubscriptionIncome);
        responseObject.setMeta("Subscribed Income retrieved successfully.");
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    @GetMapping(path = "/income/ppv/{month}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMonthlyPayPerViewIncome(@PathVariable("month") Integer month) {
        Long monthlyPayPerViewIncome = subscriptionService.getMonthlyPayPerViewIncome(month);
        responseObject.setData(monthlyPayPerViewIncome);
        responseObject.setMeta("Pay per view Income retrieved successfully.");
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    @GetMapping(path = "/subscriptions/validate")
    public ResponseEntity<?> getSubscriptions(@RequestParam("movieId") Long movieId,
            @RequestParam("availability") String a) {
        Long id = (Long) session.getAttribute("userId");
        if (id == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("401");
        }
        User u = userServ.getUserById(id);
        Movie m = movieServ.getOneMovie(movieId);

        if (u == null || m == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("401");
        } else {
            return ResponseEntity.ok(subscriptionService.getSubscriptions(u, m, a));
        }
    }

    @GetMapping(path = "/subscriptions/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllSubscriptionByUser(@PathVariable("userId") Long userId, HttpSession session) {
        try {
            Long sessionUserId = (Long) session.getAttribute("userId");
            User user = new User();
            if (userId != null) {
                user.setUserId(userId);
            } else {
                user.setUserId(sessionUserId);
            }
            List<UserSubscription> userSubscriptions = subscriptionService.getAllUserSubscriptionByUserId(user);
            if (!CollectionUtils.isEmpty(userSubscriptions)) {
                responseObject.setData(userSubscriptions);
                responseObject.setMeta("Subscription retrieved succesfully");
                return new ResponseEntity(responseObject, HttpStatus.OK);
            } else {
                responseObject.setData(null);
                responseObject.setMeta("No Subscription exists");
                return new ResponseEntity(responseObject, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseObject.setData(e);
            return new ResponseEntity(responseObject, HttpStatus.NO_CONTENT);
        }
    }

}
