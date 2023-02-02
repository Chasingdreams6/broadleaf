package com.community.core.order;

import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemServiceImpl;
import org.broadleafcommerce.core.order.service.call.ConfigurableOrderItemRequest;
import org.broadleafcommerce.core.order.service.call.OrderItemRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyOrderItemServiceImpl extends OrderItemServiceImpl {
    private final static Logger log = LoggerFactory.getLogger(MyOrderItemServiceImpl.class);
    @Override
    public void mergeOrderItemRequest(ConfigurableOrderItemRequest itemRequest, OrderItem orderItem) {
        log.info("mergeOrderItemRequest");
        super.mergeOrderItemRequest(itemRequest, orderItem);
    }

    @Override
    public void modifyOrderItemRequest(ConfigurableOrderItemRequest itemRequest) {
        log.info("modifyOrderItemRequest");
        super.modifyOrderItemRequest(itemRequest);
    }

    @Override
    public OrderItem createOrderItem(OrderItemRequest itemRequest) {
        log.info("createOrderItem");
        return super.createOrderItem(itemRequest);
    }

    @Override
    public void priceOrderItem(OrderItem item) {
        log.info("priceOrderItem");
        super.priceOrderItem(item);
    }
}
