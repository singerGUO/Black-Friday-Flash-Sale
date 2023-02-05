package com.zhenghao.seckill.service;

import com.zhenghao.seckill.db.dao.SeckillActivityDao;
import com.zhenghao.seckill.db.po.SeckillActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeckillOverSellService {
    @Autowired
    private SeckillActivityDao seckillActivityDao;

    public String processSeckill(long activityID) {
        SeckillActivity activity = seckillActivityDao.querySeckillActivityById(activityID);
        int availableStock = activity.getAvailableStock();
        String result;
        if (availableStock > 0) {
            result = "Congrats, order processed";
            System.out.println(result);
            availableStock -= 1;
            activity.setAvailableStock(availableStock);
            seckillActivityDao.updateSeckillActivity(activity);
        } else {
            result = "sorry, failed to process order";
            System.out.println(result);
        }
        return result;

    }


}
