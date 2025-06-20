package com.tourismwebsite.domain;

/**
 * @Author J
 * @Date 2025/6/13  15:54
 * @Version 1.0
 */
public class Param {
    private Integer userId;
    private Integer routeId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    @Override
    public String toString() {
        return "Param{" +
                "userId=" + userId +
                ", routeId=" + routeId +
                '}';
    }
}
