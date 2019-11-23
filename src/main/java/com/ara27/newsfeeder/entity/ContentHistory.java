package com.ara27.newsfeeder.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONTENT_HISTORY")
public class ContentHistory {
    @Id
    String id;
    String cronjobId;
    String header;

    @Column(length = 2000)
    String title;

    @Column(length = 2000)
    String subTitle;

    @Column(length = 2000)
    String url;
    String source;
    String timeStamp;
    LocalDateTime dtCreated;
    LocalDateTime dtUpdated;
    LocalDateTime dtCronRunning;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCronjobId() {
        return cronjobId;
    }

    public void setCronjobId(String cronjobId) {
        this.cronjobId = cronjobId;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LocalDateTime getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(LocalDateTime dtCreated) {
        this.dtCreated = dtCreated;
    }

    public LocalDateTime getDtUpdated() {
        return dtUpdated;
    }

    public void setDtUpdated(LocalDateTime dtUpdated) {
        this.dtUpdated = dtUpdated;
    }

    public LocalDateTime getDtCronRunning() {
        return dtCronRunning;
    }

    public void setDtCronRunning(LocalDateTime dtCronRunning) {
        this.dtCronRunning = dtCronRunning;
    }
}
