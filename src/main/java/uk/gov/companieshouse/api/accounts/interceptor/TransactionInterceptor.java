package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionManager;

@Component
public class TransactionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private TransactionManager transactionManager;

    /**
     * Pre handle method to validate the request before it reaches the controller. Check if the url
     * has an existing transaction and save it in the request's attribute. If transaction is not
     * found then return 404
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        try {
            Map<String, String> pathVariables = (Map) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

            String transactionId = pathVariables.get("transactionId");
            ResponseEntity<Transaction> transaction = transactionManager
                .getTransaction(transactionId, request.getHeader("X-Request-Id"));

            request.setAttribute(AttributeName.TRANSACTION.getValue(), transaction.getBody());
            return true;
        } catch (HttpClientErrorException httpClientErrorException) {
            response.setStatus(httpClientErrorException.getStatusCode().value());
            return false;
        }
    }
}