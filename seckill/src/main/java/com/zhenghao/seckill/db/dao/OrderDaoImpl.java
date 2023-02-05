package com.zhenghao.seckill.db.dao;

import com.zhenghao.seckill.db.mappers.OrderMapper;
import com.zhenghao.seckill.db.po.Order;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;


@Repository
public class OrderDaoImpl implements OrderDao{
    @Resource
    private OrderMapper mapper;

    @Override
    public void insertOrder(Order order) {
        mapper.insert(order);
    }

    @Override
    public Order queryOrder(String orderNo) {
        return mapper.selectByOrderNo(orderNo);
    }

    @Override
    public void updateOrder(Order order) {
        mapper.updateByPrimaryKey(order);

    }
}
