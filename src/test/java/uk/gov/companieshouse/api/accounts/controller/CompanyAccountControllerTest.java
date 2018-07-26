package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;


@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountControllerTest {

    @Mock
    private CompanyAccount companyAccount;

    @Mock
    private CompanyAccount createdCompanyAccount;

    @Mock
    private CompanyAccountService companyAccountService;

    @InjectMocks
    private CompanyAccountController companyAccountController;

    @BeforeEach
    void setUp(){
        when(companyAccountService.createCompanyAccount(companyAccount)).thenReturn(createdCompanyAccount);
    }

    @Test
    @DisplayName("Tests the successful creation of an companyAccount resource")
    void canCreateAccount() {
        ResponseEntity response = companyAccountController.createCompanyAccount(companyAccount);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(new Equals(createdCompanyAccount).matches(response.getBody()));
    }
}