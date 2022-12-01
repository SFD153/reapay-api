package com.mouni.reapay.services;

import com.mouni.reapay.models.Closer;
import com.mouni.reapay.models.requests.AddCloserRequestDto;
import com.mouni.reapay.models.responses.GetClosersResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CloserService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CloserService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<GetClosersResponseDto> getAllClosers() {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.sort(Sort.Direction.ASC, "noOrder"));
        return mongoTemplate.aggregate(aggregation, Closer.class, GetClosersResponseDto.class).getMappedResults();
    }

    public void saveClosers(List<AddCloserRequestDto> closers) {
        closers.forEach(closer -> {
            mongoTemplate.save(Closer.builder()
                    .noOrder(closer.getNoOrder())
                    .closerName(closer.getCloserName())
                    .closerAddress(closer.getCloserAddress())
                    .build());
        });
    }
}
