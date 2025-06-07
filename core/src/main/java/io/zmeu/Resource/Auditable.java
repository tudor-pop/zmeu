package io.zmeu.Resource;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.time.Instant;

@MappedSuperclass
@Data
public abstract class Auditable {

    @Column(name = "created_on", updatable = false)
    @DiffIgnore
    protected Instant createdOn;

    @Column(name = "updated_on")
    @DiffIgnore
    protected Instant updatedOn;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdOn = now;
        this.updatedOn = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = Instant.now();
    }
}