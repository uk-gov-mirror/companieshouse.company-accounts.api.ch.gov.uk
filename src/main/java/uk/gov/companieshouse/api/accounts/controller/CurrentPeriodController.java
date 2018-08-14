package uk.gov.companieshouse.api.accounts.controller;

import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.service.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@RestController
public class CurrentPeriodController {

    @Autowired
    private CurrentPeriodService currentPeriodService;

    @PostMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountsId}/small-full/current-period",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@Valid @RequestBody CurrentPeriod currentPeriod,
            HttpServletRequest request)
            throws NoSuchAlgorithmException {
        Transaction transaction = (Transaction) request.getSession().getAttribute(AttributeName.TRANSACTION.getValue());
        CurrentPeriod result = currentPeriodService
                .save(currentPeriod, transaction.getCompanyNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }
}