package com.itheima.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.api.IOrderService;
import com.itheima.entity.Result;
import com.itheima.shop.pojo.TradeOrder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderControllre {

    @Reference
    private IOrderService orderService;

    @RequestMapping("/confirm")
    public Result confirmOrder(@RequestBody TradeOrder order){
        return orderService.confirmOrder(order);
    }

}
