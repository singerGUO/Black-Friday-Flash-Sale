package com.zhenghao.seckill.component;

import com.zhenghao.seckill.db.dao.SeckillActivityDao;
import com.zhenghao.seckill.db.po.SeckillActivity;
import com.zhenghao.seckill.service.RedisService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class RedisPreheatRunner  implements ApplicationRunner {
    @Resource
    RedisService redisService;

    @Resource
    SeckillActivityDao seckillActivityDao;

    /**
     * 启动项目时 向 Redis 存入 商品库存
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<SeckillActivity> seckillActivities = seckillActivityDao.querySeckillActivitysByStatus(1);
        for (SeckillActivity seckillActivity : seckillActivities) {
            redisService.setValue("stock:" + seckillActivity.getId(),
                    (long) seckillActivity.getAvailableStock());
        }
    }
}
