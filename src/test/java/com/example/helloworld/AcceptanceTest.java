package com.example.helloworld;

import com.example.helloworld.api.Saying;
import com.example.helloworld.core.Person;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by ayache on 11/05/2016.
 */
public class AcceptanceTest {

    private static final int PORT = 8181;
    private Client client;
    private static final String JENKINS_HOST = "ec2-54-171-187-14.eu-west-1.compute.amazonaws.com";

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
    }


    @Test
    public void testHelloWorld() throws Exception {
        final Optional<String> name = Optional.fromNullable("Dr. IntegrationTest");
        final Saying saying = client.target("http://" + JENKINS_HOST + ":" + PORT + "/hello-world")
                .queryParam("name", name.get())
                .request()
                .get(Saying.class);
        assertThat(saying.getContent()).isEqualTo("Hello, Dr. IntegrationTest!");
    }

    @Test
    public void testPostPerson() throws Exception {
        final Person person = new Person("Dr. IntegrationTest", "Chief Wizard");
        final Person newPerson = client.target("http://" + JENKINS_HOST + ":"  + PORT + "/people")
                .request()
                .post(Entity.entity(person, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Person.class);
        assertThat(newPerson.getId()).isNotNull();
        assertThat(newPerson.getFullName()).isEqualTo(person.getFullName());
        assertThat(newPerson.getJobTitle()).isEqualTo(person.getJobTitle());
    }
}
