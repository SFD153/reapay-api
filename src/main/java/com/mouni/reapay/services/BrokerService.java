package com.mouni.reapay.services;

import com.mouni.reapay.models.Broker;
import com.mouni.reapay.models.PdfRequest;
import com.mouni.reapay.models.RealtorClosingTransaction;
import com.mouni.reapay.models.RealtorUser;
import com.mouni.reapay.models.requests.AddBrokerRequestDto;
import com.mouni.reapay.models.responses.GetBrokersResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BrokerService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BrokerService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<GetBrokersResponseDto> getAllBrokers() {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.sort(Sort.Direction.ASC, "noOrder"));
        return mongoTemplate.aggregate(aggregation, Broker.class, GetBrokersResponseDto.class).getMappedResults();
    }

    public void saveBrokers(List<AddBrokerRequestDto> brokers) {
        brokers.forEach(broker -> {
            mongoTemplate.save(Broker.builder()
                    .noOrder(broker.getNoOrder())
                    .brokerName(broker.getBrokerName())
                    .brokerAddress(broker.getAddress())
                    .build());
        });
    }
}
