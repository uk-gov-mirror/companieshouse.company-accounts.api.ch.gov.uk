package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.AccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Account;
import uk.gov.companieshouse.api.accounts.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class AccountServiceImplTest {

    @Mock
    private Account account;

    @Mock
    private AccountEntity createdAccountEntity;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    public void setUp() {
        when(accountRepository.insert(any(AccountEntity.class))).thenReturn(createdAccountEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of an account resource")
    public void canCreateAccount() {
        Account result = accountService.createAccount(account);
        assertNotNull(result);
        assertEquals(account, result);

    }
}