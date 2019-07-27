package com.itheima.shop.pojo;

import java.io.Serializable;

public class TradeUserMoneyLogKey implements Serializable {
    private Long userId;

    private Long orderId;

    private Integer moneyLogType;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getMoneyLogType() {
        return moneyLogType;
    }

    public void setMoneyLogType(Integer moneyLogType) {
        this.moneyLogType = moneyLogType;
    }
}