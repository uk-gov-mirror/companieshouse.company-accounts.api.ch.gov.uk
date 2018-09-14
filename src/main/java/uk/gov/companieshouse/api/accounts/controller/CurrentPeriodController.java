package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.Errors;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.service.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.BindingResultErrorToErrorMapper;
import uk.gov.companieshouse.api.accounts.validation.CurrentPeriodValidator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.LogContext;
import uk.gov.companieshouse.logging.util.LogHelper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/current-period", produces = MediaType.APPLICATION_JSON_VALUE)
public class CurrentPeriodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);
    private static final String REQUEST_ID = "X-Request-Id";

    @Autowired
    private CurrentPeriodService currentPeriodService;

    @Autowired
    private CurrentPeriodValidator currentPeriodValidator;

    @Autowired
    private BindingResultErrorToErrorMapper resultsMapper;

    @Autowired
    private ApiResponseMapper apiResponseMapper;
    
    @PostMapping
    public ResponseEntity create(@RequestBody @Valid CurrentPeriod currentPeriod,
        BindingResult bindingResult, HttpServletRequest request) {

        Errors errors = new Errors();

        if (bindingResult.hasErrors()) {

            errors = resultsMapper.mapBindingResultErrorsToErrorModel(bindingResult, errors);

        }

        currentPeriodValidator.validateCurrentPeriod(currentPeriod, errors);
        if (errors.hasErrors()) {

            LOGGER.error("Current period validation failure");
            logValidationFailureError(getRequestId(request), errors);

            return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);

        }

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        CompanyAccountEntity companyAccountEntity = (CompanyAccountEntity) request
            .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        String companyAccountId = companyAccountEntity.getId();
        String requestId = request.getHeader(REQUEST_ID);

        ResponseEntity responseEntity;

        try {
            ResponseObject<CurrentPeriod> responseObject = currentPeriodService
                .create(currentPeriod, transaction, companyAccountId, requestId);
            responseEntity = apiResponseMapper
                .map(responseObject.getStatus(), responseObject.getData(),
                    responseObject.getValidationErrorData());


        } catch (DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction_id", transaction.getId());
            LOGGER.errorRequest(request, ex, debugMap);
            responseEntity = apiResponseMapper.map(ex);
        }

        return responseEntity;
    }

    @GetMapping
    public ResponseEntity get(HttpServletRequest request) {
        LogContext logContext = LogHelper.createNewLogContext(request);

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());
        CompanyAccountEntity companyAccountEntity = (CompanyAccountEntity) request
            .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        if (companyAccountEntity == null) {
            LOGGER.error("Current Period error: No company account in request session",
                logContext);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String companyAccountId = companyAccountEntity.getId();
        String requestId = request.getHeader("X-Request-Id");
        String currentPeriodId = currentPeriodService.generateID(companyAccountId);
        ResponseObject<CurrentPeriod> responseObject;

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());

        try {
            responseObject = currentPeriodService.findById(currentPeriodId, requestId);
        } catch (DataException de) {
            LOGGER.errorRequest(request, de, debugMap);
            return apiResponseMapper.map(de);
        }

        return apiResponseMapper.mapGetResponse(responseObject.getData(), request);

    }

    /**
     * Log {@link AbridgedAccount} validation failure
     *
     * @param requestId
     * @param errors
     */

    private void logValidationFailureError(String requestId, Errors errors) {
        HashMap<String, Object> logMap = new HashMap<>();
        logMap.put("message", "Validation failure");
        logMap.put("Errors: ", errors);
        LOGGER.traceContext(requestId, "", logMap);
    }

    private String getRequestId(HttpServletRequest request) {
        return request.getHeader(REQUEST_ID);
    }
}
