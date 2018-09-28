package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.validation.CurrentPeriodValidator;
import uk.gov.companieshouse.api.accounts.validation.PreviousPeriodValidator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/previous-period", produces = MediaType.APPLICATION_JSON_VALUE)
public class PreviousPeriodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Autowired
    private PreviousPeriodService previousPeriodService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private PreviousPeriodValidator previousPeriodValidator;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody PreviousPeriod previousPeriod, BindingResult bindingResult,
        @PathVariable("companyAccountId") String companyAccountId, HttpServletRequest request) {

        Errors errors = new Errors();

        if (bindingResult.hasErrors()) {

            errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult, errors);

        }

        previousPeriodValidator.validatePreviousPeriod(previousPeriod, errors);
        if (errors.hasErrors()) {

            LOGGER.error(
                "Current period validation failure");
            logValidationFailureError(getRequestId(request), errors);

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

        }

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        String requestId = request.getHeader("X-Request-Id");

        ResponseEntity responseEntity;
        try {
            ResponseObject<PreviousPeriod> responseObject = previousPeriodService.create(previousPeriod, transaction, companyAccountId, requestId);
            responseEntity = apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                    responseObject.getValidationErrorData());

        } catch (DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction_id", transaction.getId());
            debugMap.put("company_account_id", companyAccountId);
            debugMap.put("message", "Failed to create previous period resource");
            LOGGER.errorRequest(request, ex, debugMap);
            responseEntity = apiResponseMapper.map(ex);
        }

        return responseEntity;
    }

    void logValidationFailureError(String requestId, Errors errors) {
        HashMap<String, Object> logMap = new HashMap<>();
        logMap.put("message", "Validation failure");
        logMap.put("Errors: ", errors);
        LOGGER.traceContext(requestId, "", logMap);
    }

    private String getRequestId(HttpServletRequest request) {
        return request.getHeader("X-Request-Id");
    }
}
