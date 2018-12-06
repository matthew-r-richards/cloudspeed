package com.zuhlke.smallface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class SmallfaceController {
    private final SmallfaceService smallface;

    @Autowired
    public SmallfaceController(SmallfaceService smallface) {
        this.smallface = smallface;
    }

    @GetMapping("/")
    public String hello() {
        return "Smallface is running";
    }

    @GetMapping("/stats")
    public String stats() {
        return "#users: " + smallface.getNumberOfUsers();
    }

    @PostMapping("/register")
    public void register(@RequestParam("email") String email, @RequestParam("password") String password) {
        smallface.registerUser(email, password);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean login(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session) {
        boolean passwordIsValid = smallface.authenticate(email, password);
        if (passwordIsValid) session.setAttribute("user", email);
        return passwordIsValid;
    }

    @GetMapping(value = "posts/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getRecentPosts(HttpSession session) {
        return smallface.getAllFriendsPosts(getCurrentUser(session));
    }

    @PutMapping("friends/{email}")
    public void addFriend(@PathVariable("email") String friendEmail, HttpSession session) {
        smallface.addFriend(getCurrentUser(session), friendEmail);
    }

    @PostMapping(value = "posts/", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void addPost(@RequestBody String content, HttpSession session) {
        smallface.addPost(getCurrentUser(session), content);
    }

    private String getCurrentUser(HttpSession session) {
        return (String) session.getAttribute("user");
    }
}