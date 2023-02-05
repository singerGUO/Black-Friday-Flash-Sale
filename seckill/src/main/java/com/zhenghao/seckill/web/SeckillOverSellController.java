package com.zhenghao.seckill.web;

import com.zhenghao.seckill.service.SeckillActivityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class SeckillOverSellController {
//    @Resource
//    private SeckillOverSellService seckillOverSellService;
    @Resource
    private SeckillActivityService seckillActivityService;
    /**
     * 没经过redis处理的超卖判断路基，经不起高并发
     *
     */
//    @ResponseBody
//    @RequestMapping("/seckill/{seckillActivityId}")
//    public String seckill(@PathVariable long seckillActivityId){
//        return seckillOverSellService.processSeckill(seckillActivityId);
//    }

    /**
     * 使用 lua 脚本处理抢购请求
     * @param seckillActivityId
     * @return
     */
    @ResponseBody
    @RequestMapping("/seckill/{seckillActivityId}")
    public String seckillCommodity(@PathVariable long seckillActivityId) {
        boolean stockValidateResult = seckillActivityService.seckillStockValidator(seckillActivityId);
        return stockValidateResult ? "恭喜你秒杀成功" : "商品已经售完，下次再来";
    }

}
