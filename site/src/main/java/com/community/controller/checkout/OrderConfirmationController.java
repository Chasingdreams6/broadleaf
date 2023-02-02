/*-
 * #%L
 * Community Demo Site
 * %%
 * Copyright (C) 2009 - 2022 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

package com.community.controller.checkout;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.web.controller.checkout.BroadleafOrderConfirmationController;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.core.web.order.OrderState;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class OrderConfirmationController extends BroadleafOrderConfirmationController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @RequestMapping(value = "/confirmation/{orderNumber}", method = RequestMethod.GET)
    public String displayOrderConfirmationByOrderNumber(@PathVariable("orderNumber") String orderNumber, Model model,
            HttpServletRequest request, HttpServletResponse response) {
        Order order = orderService.findOrderByOrderNumber(orderNumber);
        String key = order.getCustomer().getId().toString();
        String value = order.getCustomer().getId() + "," + order.getOrderItems().size();
        for (OrderItem item : order.getOrderItems()) {
            value = value +  "," + item.getName();
        }
        System.out.println(value);
        kafkaTemplate.send("test02", key, value);
        return super.displayOrderConfirmationByOrderNumber(orderNumber, model, request, response);
    }

}
