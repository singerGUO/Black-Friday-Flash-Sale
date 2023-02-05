package com.zhenghao.seckill.db.dao;

import com.zhenghao.seckill.db.po.Order;

public interface OrderDao {
    void insertOrder(Order order);

    Order queryOrder(String orderNo);

    void updateOrder(Order order);

}
