package com.zhenghao.seckill.service;


import com.alibaba.fastjson.JSON;
import com.zhenghao.seckill.db.dao.OrderDao;
import com.zhenghao.seckill.db.dao.SeckillActivityDao;
import com.zhenghao.seckill.db.dao.SeckillCommodityDao;
import com.zhenghao.seckill.db.po.Order;
import com.zhenghao.seckill.db.po.SeckillActivity;
import com.zhenghao.seckill.db.po.SeckillCommodity;
import com.zhenghao.seckill.mq.RocketMQService;
import com.zhenghao.seckill.util.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
@Slf4j
@Service
public class SeckillActivityService {
    @Resource
    private OrderDao orderDao;

    @Resource
    private RedisService service;

    @Resource
    private SeckillActivityDao seckillActivityDao;
    @Resource
    SeckillCommodityDao seckillCommodityDao;
    @Resource
    private RocketMQService rocketMQService;
    /**
     * datacenterId;  数据中心
     * machineId;     机器标识
     * 在分布式环境中可以从机器配置上读取
     * 单机开发环境中先写死
     */
    private SnowFlake snowFlake = new SnowFlake(1,1);

    public boolean seckillStockValidator(long activityId){
        String key = "stock:" + activityId;
        return service.stockDeductValidator(key);
    }
    public Order createOrder(long seckillActivityID, long userID) throws Exception{
        SeckillActivity activity = seckillActivityDao.querySeckillActivityById(seckillActivityID);
        Order order = new Order();
        //snowflake algorithm
        order.setOrderNo(String.valueOf(snowFlake.nextId()));
        order.setSeckillActivityId(activity.getId());
        order.setUserId(userID);
        order.setOrderAmount(activity.getSeckillPrice().longValue());
        //send create order message
        rocketMQService.sendMessage("seckill_order", JSON.toJSONString(order));
        /*
         * 3.发送订单付款状态校验消息
         * 开源RocketMQ支持延迟消息，但是不支持秒级精度。默认支持18个level的延迟消息，这是通过broker端的messageDelayLevel配置项确定的，如下：
         * messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
         */
        rocketMQService.sendDelayMessage("pay_check", JSON.toJSONString(order), 3);
        return order;

    }
    /**
     * 将秒杀详情相关信息倒入redis
     *
     * @param seckillActivityId
     */
    public void pushSeckillInfoToRedis(long seckillActivityId) {
        SeckillActivity seckillActivity = seckillActivityDao.querySeckillActivityById(seckillActivityId);
        service.setValue("seckillActivity:" + seckillActivityId, JSON.toJSONString(seckillActivity));

        SeckillCommodity seckillCommodity = seckillCommodityDao.querySeckillCommodityById(seckillActivity.getCommodityId());
        service.setValue("seckillCommodity:" + seckillActivity.getCommodityId(), JSON.toJSONString(seckillCommodity));
    }



    /**
     * 订单支付完成处理
     *
     * @param orderNo
     */
    public void payOrderProcess(String orderNo) throws Exception {
        log.info("完成支付订单  订单号：" + orderNo);
        Order order = orderDao.queryOrder(orderNo);
        /*
         * 1.判断订单是否存在
         * 2.判断订单状态是否为未支付状态
         */
        if (order == null) {
            log.error("订单号对应订单不存在：" + orderNo);
            return;
        } else if(order.getOrderStatus() != 1 ) {
            log.error("订单状态无效：" + orderNo);
            return;
        }
        /*
         * 2.订单支付完成
         */
        order.setPayTime(new Date());
        //订单状态 0:没有可用库存，无效订单 1:已创建等待付款 ,2:支付完成
        order.setOrderStatus(2);
        orderDao.updateOrder(order);
        /*
         *3.发送订单付款成功消息
         */
        rocketMQService.sendMessage("pay_done", JSON.toJSONString(order));
    }
}
