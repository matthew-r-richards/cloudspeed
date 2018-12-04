package com.zuhlke.smallface;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class SmallfaceControllerIntegrationTest {
    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;
    @Autowired UserRepository users;
    private String sessionID;

    @Test
    public void testRegisteringUser() throws Exception {
        String email = "joe@gmail.com";
        String password = "asdf";
        register(email, password);
    }

    @Test
    public void testLoggingIn() throws Exception {
        register("john@gmail.com", "1234");
        assertTrue(login("john@gmail.com", "1234"));
    }

    @Test
    public void testPostingStuff() throws Exception {
        register("bill@gmail.com", "1234");
        assertTrue(login("bill@gmail.com", "1234"));
        post("Hello World!");
    }

    @Test
    public void addingFrield() throws Exception {
        register("alice@gmail.com", "1234");
        register("bob@gmail.com", "1234");
        assertTrue(login("bob@gmail.com", "1234"));
        addFriend("alice@gmail.com");
    }

    @Test
    public void testGettingPostsFromFriends() throws Exception {
        register("charlie@gmail.com", "1234");
        register("donna@gmail.com", "1234");
        assertTrue(login("donna@gmail.com", "1234"));
        addFriend("charlie@gmail.com");
        post("Hello Charlie!");
        assertTrue(login("charlie@gmail.com", "1234"));
        assertTrue(getPosts().contains("Hello Charlie!"));
    }

    private String getPosts() {
        ResponseEntity<String> response = rest.exchange("http://localhost:" + port + "/posts/", GET, useSession(null), String.class);
        assertEquals(response.getStatusCode(), OK);
        return response.getBody();
    }

    private void addFriend(String email) {
        ResponseEntity<Void> response = rest.exchange("http://localhost:" + port + "/friends/" + email, PUT, useSession(null), Void.class);
        assertEquals(response.getStatusCode(), OK);
    }

    private void post(String message) {
        ResponseEntity<Void> response = rest.exchange("http://localhost:" + port + "/posts/", POST, useSession(message), Void.class);
        assertEquals(response.getStatusCode(), OK);
    }

    private HttpEntity<String> useSession(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "JSESSIONID=" + sessionID);
        return new HttpEntity<>(message, headers);
    }

    private void register(String email, String password) {
        ResponseEntity<String> response = rest.postForEntity("http://localhost:" + port + "/register?email=" + email + "&password=" + password, null, String.class);
        assertEquals(response.getStatusCode(), OK);
    }

    private boolean login(String email, String password) {
        ResponseEntity<Boolean> response = rest.postForEntity("http://localhost:" + port + "/login?email=" + email + "&password=" + password, null, Boolean.class);
        sessionID = response.getHeaders().get("Set-Cookie").get(0).replaceAll("JSESSIONID=", "");
        assertEquals(response.getStatusCode(), OK);
        return response.getBody();
    }
}