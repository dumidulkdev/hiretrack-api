package com.hiretrack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hiretrack")
public class HireTrackProperties {

    private int ghostingThresholdDays = 21;
    private Scheduler scheduler = new Scheduler();
    private Pagination pagination = new Pagination();

    public int getGhostingThresholdDays() {
        return ghostingThresholdDays;
    }

    public void setGhostingThresholdDays(int ghostingThresholdDays) {
        this.ghostingThresholdDays = ghostingThresholdDays;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public static class Scheduler {
        private String dailyCron = "0 0 6 * * *";

        public String getDailyCron() {
            return dailyCron;
        }

        public void setDailyCron(String dailyCron) {
            this.dailyCron = dailyCron;
        }
    }

    public static class Pagination {
        private int maxPageSize = 100;

        public int getMaxPageSize() {
            return maxPageSize;
        }

        public void setMaxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
        }
    }
}