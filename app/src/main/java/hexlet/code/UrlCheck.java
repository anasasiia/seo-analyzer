package hexlet.code;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
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

    long statusCode;

    String title;

    String h1;

    @Lob
    String description;

    @Basic(optional = false)
    @ManyToOne(cascade = CascadeType.PERSIST)
    Url url;

    public UrlCheck(String title, String h1, String description, long statusCode) {
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.statusCode = statusCode;
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

    public long getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }

    public String getH1() {
        return h1;
    }
}
