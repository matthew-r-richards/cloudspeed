package com.zuhlke.smallface;

import com.zuhlke.smallface.entities.Post;
import com.zuhlke.smallface.entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryIntegrationTest {

    @Autowired UserRepository users;
    @Autowired
    TransactionTemplate template;

    @Test
    @Transactional
    public void testCreatingUser() throws Exception {
        User user = new User("simon@gmail.com", "123");
        users.save(user);
        User retrievedUser = users.getOne("simon@gmail.com");
        assertEquals(user.getEmail(), retrievedUser.getEmail());
        assertEquals(user.getPassword(), retrievedUser.getPassword());
    }

    @Test
    public void testAddingPost() throws Exception {
        template.execute(transactionStatus -> {
            User user = new User("fred@gmail.com", "123");
            user.addPost(new Post("Hello World!"));
            users.save(user);
            return null;
        });
        template.execute(transactionStatus -> {
            User user = users.getOne("fred@gmail.com");
            assertEquals(1, user.getPosts().size());
            return null;
        });
    }
}