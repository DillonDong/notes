package com.itheima.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.itheima.api.IPayService;
import com.itheima.constant.ShopCode;
import com.itheima.entity.Result;
import com.itheima.exception.CastException;
import com.itheima.shop.mapper.TradeMqProducerTempMapper;
import com.itheima.shop.mapper.TradePayMapper;
import com.itheima.shop.pojo.TradeMqProducerTemp;
import com.itheima.shop.pojo.TradePay;
import com.itheima.shop.pojo.TradePayExample;
import com.itheima.utils.IDWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;


@Slf4j
@Component
@Service(interfaceClass = IPayService.class)
public class PayServiceImpl implements IPayService{

    @Autowired
    private TradePayMapper tradePayMapper;

    @Autowired
    private TradeMqProducerTempMapper mqProducerTempMapper;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private IDWorker idWorker;

    @Value("${rocketmq.producer.group}")
    private String groupName;

    @Value("${mq.topic}")
    private String topic;

    @Value("${mq.pay.tag}")
    private String tag;

    @Override
    public Result createPayment(TradePay tradePay) {

        if(tradePay==null || tradePay.getOrderId()==null){
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }

        //1.判断订单支付状态
        TradePayExample example = new TradePayExample();
        TradePayExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(tradePay.getOrderId());
        criteria.andIsPaidEqualTo(ShopCode.SHOP_PAYMENT_IS_PAID.getCode());
        int r = tradePayMapper.countByExample(example);
        if(r>0){
            CastException.cast(ShopCode.SHOP_PAYMENT_IS_PAID);
        }
        //2.设置订单的状态为未支付
        tradePay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        //3.保存支付订单
        tradePay.setPayId(idWorker.nextId());
        tradePayMapper.insert(tradePay);

        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
    }

    @Override
    public Result callbackPayment(TradePay tradePay) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        log.info("支付回调");
        //1. 判断用户支付状态
        if(tradePay.getIsPaid().intValue()==ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode().intValue()){
            //2. 更新支付订单状态为已支付
            Long payId = tradePay.getPayId();
            TradePay pay = tradePayMapper.selectByPrimaryKey(payId);
            //判断支付订单是否存在
            if(pay==null){
                CastException.cast(ShopCode.SHOP_PAYMENT_NOT_FOUND);
            }
            pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
            int r = tradePayMapper.updateByPrimaryKeySelective(pay);
            log.info("支付订单状态改为已支付");
            if(r==1){
                //3. 创建支付成功的消息
                TradeMqProducerTemp tradeMqProducerTemp = new TradeMqProducerTemp();
                tradeMqProducerTemp.setId(String.valueOf(idWorker.nextId()));
                tradeMqProducerTemp.setGroupName(groupName);
                tradeMqProducerTemp.setMsgTopic(topic);
                tradeMqProducerTemp.setMsgTag(tag);
                tradeMqProducerTemp.setMsgKey(String.valueOf(tradePay.getPayId()));
                tradeMqProducerTemp.setMsgBody(JSON.toJSONString(tradePay));
                tradeMqProducerTemp.setCreateTime(new Date());
                //4. 将消息持久化数据库
                mqProducerTempMapper.insert(tradeMqProducerTemp);
                log.info("将支付成功消息持久化到数据库");

                //在线程池中进行处理
                threadPoolTaskExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        //5. 发送消息到MQ
                        SendResult result = null;
                        try {
                            result = sendMessage(topic, tag, String.valueOf(tradePay.getPayId()), JSON.toJSONString(tradePay));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(result.getSendStatus().equals(SendStatus.SEND_OK)){
                            log.info("消息发送成功");
                            //6. 等待发送结果,如果MQ接受到消息,删除发送成功的消息
                            mqProducerTempMapper.deleteByPrimaryKey(tradeMqProducerTemp.getId());
                            log.info("持久化到数据库的消息删除");
                        }
                    }
                });

            }
            return new Result(ShopCode.SHOP_SUCCESS.getSuccess(),ShopCode.SHOP_SUCCESS.getMessage());
        }else{
            CastException.cast(ShopCode.SHOP_PAYMENT_PAY_ERROR);
            return new Result(ShopCode.SHOP_FAIL.getSuccess(),ShopCode.SHOP_FAIL.getMessage());
        }


    }

    /**
     * 发送支付成功消息
     * @param topic
     * @param tag
     * @param key
     * @param body
     */
    private SendResult sendMessage(String topic, String tag, String key, String body) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        if(StringUtils.isEmpty(topic)){
            CastException.cast(ShopCode.SHOP_MQ_TOPIC_IS_EMPTY);
        }
        if(StringUtils.isEmpty(body)){
            CastException.cast(ShopCode.SHOP_MQ_MESSAGE_BODY_IS_EMPTY);
        }
        Message message = new Message(topic,tag,key,body.getBytes());
        SendResult sendResult = rocketMQTemplate.getProducer().send(message);
        return sendResult;
    }
}
