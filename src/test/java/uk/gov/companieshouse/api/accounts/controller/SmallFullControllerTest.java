package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Equals;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private SmallFull smallFull;

    @Mock
    private SmallFull createdSmallFull;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private HttpSession httpSessionMock;

    @InjectMocks
    private SmallFullController smallFullController;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {
        doReturn(httpSessionMock).when(request).getSession();
        doReturn(transaction).when(httpSessionMock).getAttribute(AttributeName.TRANSACTION.getValue());
        doReturn(createdSmallFull).when(smallFullService).save(any(SmallFull.class), anyString());
        doReturn("123456").when(transaction).getCompanyNumber();
    }

    @Test
    @DisplayName("Tests the successful creation of a smallFull resource")
    public void canCreateSmallFull() throws NoSuchAlgorithmException {
        ResponseEntity response = smallFullController.create(smallFull, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(new Equals(createdSmallFull).matches(response.getBody()));
    }
}