package com.community.core.order;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.OrderServiceImpl;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component
public class MyOrderServiceImpl extends OrderServiceImpl {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    private final static Logger log = LoggerFactory.getLogger(MyOrderServiceImpl.class);

    @Override
    @Transactional("blTransactionManager")
    public Order confirmOrder(Order order) {
        Order res = super.confirmOrder(order);
        String key = order.getOrderNumber();
        String value = order.getName() + "," + order.getOrderItems().size();
        for (OrderItem item : order.getOrderItems()) {
            value = value +  "," + item.getName();
        }
        kafkaTemplate.send("test02", key, value);
        log.info("ConfirmOrder-sendMSG");
        return res;
    }

    @Override
    public Order save(Order order, Boolean priceOrder) throws PricingException {
        String key = order.getOrderNumber();
        String value = order.getName() + "," + order.getOrderItems().size();
        for (OrderItem item : order.getOrderItems()) {
            value = value +  "," + item.getName();
        }
        kafkaTemplate.send("test02", key, value);
        log.info("SaveOrder-SendMSG");
        return super.save(order, priceOrder);
    }

    @Override
    public OrderPayment addPaymentToOrder(Order order, OrderPayment payment, Referenced securePaymentInfo) {
        log.info("addPayment");
        return super.addPaymentToOrder(order, payment, securePaymentInfo);
    }

    @Override
    public Order findOrderById(Long orderId) {
        log.info("findOrderById");
        return super.findOrderById(orderId);
    }
}

