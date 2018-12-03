package uk.gov.companieshouse.api.accounts.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.Statement;
import uk.gov.companieshouse.api.accounts.service.impl.StatementService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/statements",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class StatementsController {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String TRANSACTION_ID_KEY = "transaction_id";
    private static final String COMPANY_ACCOUNT_ID_KEY = "id";

    @Autowired
    private StatementService statementService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Statement statement,
        @PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        Transaction transaction =
            (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Statement> responseObject =
                statementService.create(statement, transaction, companyAccountId, request);

            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors());

        } catch (DataException ex) {
            LOGGER.errorRequest(request, ex, getDebugMap(TRANSACTION_ID_KEY, transaction.getId()));

            return apiResponseMapper.map(ex);
        }
    }

    @PutMapping
    public ResponseEntity update(@Valid @RequestBody Statement statement,
        @PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        Transaction transaction =
            (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Statement> responseObject =
                statementService.update(statement, transaction, companyAccountId, request);

            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors());

        } catch (DataException ex) {
            LOGGER.errorRequest(request, ex, getDebugMap(TRANSACTION_ID_KEY, transaction.getId()));

            return apiResponseMapper.map(ex);
        }
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        String statementId = statementService.generateID(companyAccountId);

        try {
            ResponseObject<Statement> responseObject =
                statementService.findById(statementId, request);

            return apiResponseMapper.mapGetResponse(responseObject.getData(), request);

        } catch (DataException ex) {
            LOGGER.errorRequest(request, ex, getDebugMap(COMPANY_ACCOUNT_ID_KEY, companyAccountId));

            return apiResponseMapper.map(ex);
        }
    }

    private Map<String, Object> getDebugMap(String mapKey, String mapValue) {
        Map<String, Object> debugMap = new HashMap<>();
        debugMap.put(mapKey, mapValue);

        return debugMap;
    }
}