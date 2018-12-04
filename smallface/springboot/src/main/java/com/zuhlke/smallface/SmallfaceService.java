package com.zuhlke.smallface;

import com.zuhlke.smallface.entities.Post;
import com.zuhlke.smallface.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service
public class SmallfaceService {
    private final UserRepository users;

    @Autowired
    public SmallfaceService(UserRepository users) {
        this.users = users;
    }

    @Transactional
    public void addPost(String userEmail, String content) {
        User user = users.findById(userEmail).orElseThrow(userNotFound(userEmail));
        user.addPost(new Post(content));
        users.save(user);

        user = users.getOne(userEmail);
    }

    @Transactional
    public void addFriend(String userEmail1, String userEmail2) {
        if (userEmail1.equals(userEmail2)) return; // cannot add self
        if (userEmail1.compareTo(userEmail2) < 0) {
            String temp = userEmail1;
            userEmail1 = userEmail2;
            userEmail2 = temp;
        }
        User u1 = users.findById(userEmail1).orElseThrow(userNotFound(userEmail1));
        User u2 = users.findById(userEmail2).orElseThrow(userNotFound(userEmail2));
        u1.addFriend(u2);
        users.save(u1);
        users.save(u2);
    }

    public List<String> getAllFriendsPosts(String userEmail) {
        return users.findById(userEmail).map(user ->
                user.getFriends().stream()
                .flatMap(friend -> friend.getPosts().stream())
                .sorted(comparing(Post::getDate))
                .map(Post::getContent)
                .limit(10)
                .collect(toList())
        ).orElseThrow(userNotFound(userEmail));
    }

    public boolean authenticate(String email, String password) {
        return users.findById(email).map(user -> user.getPassword().equals(password)).orElse(false);
    }

    @Transactional
    public void registerUser(String email, String password) {
        if (!users.existsById(email)) {
            users.save(new User(email, password));
        } else {
            throw new RuntimeException("user " + email + " already registered");
        }
    }

    private Supplier<RuntimeException> userNotFound(String userEmail1) {
        return () -> new RuntimeException("user " + userEmail1 + " not found");
    }
}
