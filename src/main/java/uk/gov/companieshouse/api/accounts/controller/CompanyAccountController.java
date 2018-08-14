package uk.gov.companieshouse.api.accounts.controller;

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
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@RestController
public class CompanyAccountController {

    @Autowired
    private CompanyAccountService companyAccountService;

    @PostMapping(value = "/transactions/{transactionId}/company-accounts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCompanyAccount(@Valid @RequestBody CompanyAccount companyAccount,
            HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getSession().getAttribute(AttributeName.TRANSACTION.getValue());

        String requestId = request.getHeader("X-Request-Id");
        ResponseObject response = companyAccountService.createCompanyAccount(companyAccount, transaction, requestId);

        switch(response.getStatus()) {
            case SUCCESS:
                return ResponseEntity.status(HttpStatus.CREATED).body(response.getData());
            case DUPLICATE_KEY_ERROR:
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}