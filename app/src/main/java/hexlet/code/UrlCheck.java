package hexlet.code;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
public class UrlCheck extends Model {
    @Id
    long id;

    @WhenCreated
    Instant createdAt;

    int statusCode;

    String title;

    String h1;

    @Lob
    String description;

    @ManyToOne
    Url url;

    public UrlCheck(String title, String h1, String description, int statusCode, Url url) {
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.statusCode = statusCode;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Url getUrl() {
        return url;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }

    public String getH1() {
        return h1;
    }
}
