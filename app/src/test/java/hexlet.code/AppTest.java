package hexlet.code;

import hexlet.code.query.QUrl;
import hexlet.code.query.QUrlCheck;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private static Javalin app;
    private static String baseUrl;
    private static Database database;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() throws IOException {
        database.script().run("/truncate.sql");
        database.script().run("/seed-test-db.sql");
    }

    @Test
    void testRoot() {
        HttpResponse<String> response = Unirest.get(baseUrl).asString();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Nested
    class UrlTest {

        @Test
        void testListUrls() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("https://www.youtube.com");
        }

        @Test
        void testCreateUrl() {
            String url = "https://ru.pinterest.com";
            HttpResponse<String> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", url)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(302);
            assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains(url);

            Url actualUrl = new QUrl()
                    .name.iequalTo(url)
                    .findOne();

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.getName()).isEqualTo(url);
        }

        @Test
        void testShowUrl() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls/1")
                    .asString();

            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(body).contains("https://ru.hexlet.io");
        }

        @Test
        void testCreateBadUrl() {
            String url = "httlsjfsdlf://ru.hexlet.io";
            HttpResponse<String> responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", url)
                    .asEmpty();

            Url actualUrl = new QUrl()
                    .name.iequalTo(url)
                    .findOne();

            assertThat(actualUrl).isNull();
        }
    }

    @Test
    void testMockWebServer() throws IOException {
        String htmlPage = Files.readString(Path.of("src/test/resources/testPage.html"));
        MockWebServer server = new MockWebServer();
        String url = server.url("/").toString();
        server.enqueue(new MockResponse().setBody(htmlPage));

        HttpResponse<String> responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", url)
                .asEmpty();

        Url actualUrl = new QUrl()
                .name.iequalTo(url.substring(0, url.length() - 1))
                .findOne();

        assertThat(actualUrl).isNotNull();

        HttpResponse<String> responsePost2 = Unirest
                .post(baseUrl + "/urls/" + actualUrl.getId() + "/checks")
                .asEmpty();

        HttpResponse<String> responsePost3 = Unirest
                .get(baseUrl + "/urls/" + actualUrl.getId())
                .asString();

        UrlCheck check = new QUrlCheck()
                .findList()
                .get(0);

        assertThat(check).isNotNull();

        assertThat(responsePost3.getBody()).contains("Title");
        assertThat(responsePost3.getBody()).contains("Text");

        assertThat(check.getTitle()).isEqualTo("Title");
        assertThat(check.getH1()).isEqualTo("Text");
        assertThat(check.getDescription()).isEqualTo("description");

        server.shutdown();
    }

}


