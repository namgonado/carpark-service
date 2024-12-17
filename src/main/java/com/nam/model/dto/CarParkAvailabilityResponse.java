package com.nam.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class CarParkAvailabilityResponse {

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Data
    public static class Item {
        @JsonProperty("carpark_data")
        private List<CarParkData> carparkData;

        @JsonProperty("timestamp")
        private String timestamp;
    }

    public static class CarParkData {
        private String carparkNumber;
        private String updateDatetime;
        private List<CarParkInfo> carparkInfo;

        @JsonProperty("carpark_number")
        public String getCarparkNumber() {
            return carparkNumber;
        }

        public void setCarparkNumber(String carparkNumber) {
            this.carparkNumber = carparkNumber;
        }

        @JsonProperty("update_datetime")
        public String getUpdateDatetime() {
            return updateDatetime;
        }

        public void setUpdateDatetime(String updateDatetime) {
            this.updateDatetime = updateDatetime;
        }

        @JsonProperty("carpark_info")
        public List<CarParkInfo> getCarparkInfo() {
            return carparkInfo;
        }

        public void setCarparkInfo(List<CarParkInfo> carparkInfo) {
            this.carparkInfo = carparkInfo;
        }
    }

    public static class CarParkInfo {
        private String totalLots;
        private String lotType;
        private String lotsAvailable;

        @JsonProperty("total_lots")
        public String getTotalLots() {
            return totalLots;
        }

        public void setTotalLots(String totalLots) {
            this.totalLots = totalLots;
        }

        @JsonProperty("lot_type")
        public String getLotType() {
            return lotType;
        }

        public void setLotType(String lotType) {
            this.lotType = lotType;
        }

        @JsonProperty("lots_available")
        public String getLotsAvailable() {
            return lotsAvailable;
        }

        public void setLotsAvailable(String lotsAvailable) {
            this.lotsAvailable = lotsAvailable;
        }
    }
}
