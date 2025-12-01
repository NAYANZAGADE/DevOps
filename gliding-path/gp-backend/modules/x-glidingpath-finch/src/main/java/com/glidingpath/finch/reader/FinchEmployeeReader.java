package com.glidingpath.finch.reader;

import com.glidingpath.finch.service.impl.FinchClientFactory;
import com.tryfinch.api.client.FinchClient;
import com.tryfinch.api.models.HrisDirectoryListPage;
import com.tryfinch.api.models.IndividualInDirectory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class FinchEmployeeReader implements ItemReader<IndividualInDirectory> {

    @Autowired
    private FinchClientFactory finchClientFactory;

    private Iterator<IndividualInDirectory> employeeIterator;
    private String tenantId;

    public void initialize(String tenantId) throws Exception {
        this.tenantId = tenantId;
        this.employeeIterator = null;
    }

    @Override
    public IndividualInDirectory read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (employeeIterator == null) {
            initializeIterator();
        }

        return employeeIterator != null && employeeIterator.hasNext() ? employeeIterator.next() : null;
    }

    private void initializeIterator() {
        try {
            FinchClient client = finchClientFactory.createClient(tenantId);
            HrisDirectoryListPage directoryPage = client.hris().directory().list();
            
            List<IndividualInDirectory> allEmployees = new java.util.ArrayList<>();
            for (IndividualInDirectory employee : directoryPage.autoPager()) {
                allEmployees.add(employee);
            }
            
            this.employeeIterator = allEmployees.iterator();
            
        } catch (Exception e) {
            log.error("Failed to initialize employee iterator for tenantId: {}", tenantId, e);
            throw new RuntimeException("Failed to fetch employees from Finch", e);
        }
    }
} 