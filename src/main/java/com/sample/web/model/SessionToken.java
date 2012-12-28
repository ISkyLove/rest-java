package com.sample.web.model;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Generated on behalf of C24 Technologies Ltd.
 *
 * @version 1.0
 * @author: Iain Porter iain.porter
 * @since 28/12/2012
 */
@Entity
@Table(name="rest_session_token")
public class SessionToken extends AbstractPersistable<Long> implements Comparable<SessionToken>{

    @Column(length=36)
    private String token;

    private Date timeCreated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public SessionToken() {}

    public SessionToken(User user) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.timeCreated = new Date();
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public int compareTo(SessionToken userSession) {
        return this.timeCreated.compareTo(userSession.getTimeCreated());
    }
}
