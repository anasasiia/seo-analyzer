package hexlet.code;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;

@Entity
public class Url extends Model {
    @Id
    long id;

    String name;

    @WhenCreated
    Instant createdAt;

    @OneToMany(mappedBy = "url")
    List<UrlCheck> checks;

    public Url(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }
}
