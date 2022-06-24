package com.loginregister.logincodejavanet.KafkaService;

import com.loginregister.logincodejavanet.common.AppConstants;
import com.loginregister.logincodejavanet.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final Logger logger =
            LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void saveCreatedOrderLog(OrderDTO orderDTO)
    {
        logger.info(String.format("Order has been created, %s", orderDTO));
        this.kafkaTemplate.send(AppConstants.TOPIC_NAME_ORDER, orderDTO);
    }
}
