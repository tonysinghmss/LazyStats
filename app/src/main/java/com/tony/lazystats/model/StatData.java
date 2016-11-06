package com.tony.lazystats.model;

/**
 * Created by tony on 6/11/16.
 */

public class StatData {
    public String statDataId, statFk, createdOn;
    public long statData;

    public String getStatDataId() {
        return statDataId;
    }

    public void setStatDataId(String statDataId) {
        this.statDataId = statDataId;
    }

    public String getStatFk() {
        return statFk;
    }

    public void setStatFk(String statFk) {
        this.statFk = statFk;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public long getStatData() {
        return statData;
    }

    public void setStatData(long statData) {
        this.statData = statData;
    }
}
