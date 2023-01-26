package hexlet.code;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Entity
public class Url extends Model {
    @Id
    long id;

    String name;

    @WhenCreated
    Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL)
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

    public List<UrlCheck> getChecks() {
        return checks;
    }

    public Instant getCreatedAtOfLastCheck() {
        if (!checks.isEmpty()) {
            UrlCheck lastCheck = checks.stream()
                    .max(Comparator.comparing(UrlCheck::getId))
                    .orElseThrow(NoSuchElementException::new);
            return lastCheck.getCreatedAt();
        }
        return null;
    }

    public Integer getStatusCodeOfLastCheck() {
        if (!checks.isEmpty()) {
            UrlCheck lastCheck = checks.stream()
                    .max(Comparator.comparing(UrlCheck::getId))
                    .orElseThrow(NoSuchElementException::new);
            return lastCheck.getStatusCode();
        }
        return null;
    }
}
