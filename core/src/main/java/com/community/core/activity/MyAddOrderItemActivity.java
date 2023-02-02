package com.community.core.activity;

import com.community.core.order.MyOrderItemServiceImpl;
import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.order.service.workflow.add.AddOrderItemActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAddOrderItemActivity extends AddOrderItemActivity {
    private final static Logger log = LoggerFactory.getLogger(MyAddOrderItemActivity.class);
    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        log.info("MyAddOrderItemActivityExecute");
        return super.execute(context);
    }
}
