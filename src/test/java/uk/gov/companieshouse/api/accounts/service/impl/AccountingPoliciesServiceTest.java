package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPolicies;
import uk.gov.companieshouse.api.accounts.repository.AccountingPoliciesRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.AccountingPoliciesTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountingPoliciesServiceTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private AccountingPolicies accountingPolicies;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionLinks transactionLinks;

    @Mock
    private AccountingPoliciesRepository repository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private AccountingPoliciesTransformer transformer;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private AccountingPoliciesService service;

    private AccountingPoliciesEntity accountingPoliciesEntity;

    private static final String SELF_LINK = "self_link";
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String RESOURCE_ID = "resourceId";

    @BeforeEach
    void setUp() {

        AccountingPoliciesDataEntity dataEntity = new AccountingPoliciesDataEntity();

        Map<String, String> links = new HashMap<>();
        links.put(BasicLinkType.SELF.getLink(), SELF_LINK);
        dataEntity.setLinks(links);

        accountingPoliciesEntity = new AccountingPoliciesEntity();
        accountingPoliciesEntity.setData(dataEntity);

        when(keyIdGenerator
                .generate(COMPANY_ACCOUNTS_ID + "-" + ResourceName.ACCOUNTING_POLICIES.getName()))
                        .thenReturn(RESOURCE_ID);
    }

    @Test
    @DisplayName("Tests the successful creation of an AccountingPolicies resource")
    void canCreateAnAccountingPolicies() throws DataException {

        when(transformer.transform(accountingPolicies)).thenReturn(accountingPoliciesEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<AccountingPolicies> result = service.create(accountingPolicies, transaction,
                                                COMPANY_ACCOUNTS_ID, request);

        assertNotNull(result);
        assertEquals(accountingPolicies, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating an AccountingPolicies resource")
    void createAccountingPoliciesDuplicateKey() throws DataException {

        doReturn(accountingPoliciesEntity).when(transformer).transform(ArgumentMatchers
                .any(AccountingPolicies.class));
        when(repository.insert(accountingPoliciesEntity)).thenThrow(duplicateKeyException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject response = service.create(accountingPolicies, transaction, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating an AccountingPolicies")
    void createAccountingPoliciesMongoExceptionFailure() {

        doReturn(accountingPoliciesEntity).when(transformer).transform(ArgumentMatchers
                .any(AccountingPolicies.class));
        when(repository.insert(accountingPoliciesEntity)).thenThrow(mongoException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class,
                () -> service.create(accountingPolicies, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful update of an AccountingPolicies resource")
    void canUpdateAnAccountingPolicies() throws DataException {

        when(transformer.transform(accountingPolicies)).thenReturn(accountingPoliciesEntity);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        ResponseObject<AccountingPolicies> result = service.update(accountingPolicies, transaction,
                COMPANY_ACCOUNTS_ID, request);

        assertNotNull(result);
        assertEquals(accountingPolicies, result.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when updating an AccountingPolicies")
    void updateAccountingPoliciesMongoExceptionFailure() {

        doReturn(accountingPoliciesEntity).when(transformer).transform(ArgumentMatchers
                .any(AccountingPolicies.class));
        when(repository.save(accountingPoliciesEntity)).thenThrow(mongoException);

        when(transaction.getLinks()).thenReturn(transactionLinks);
        when(transactionLinks.getSelf()).thenReturn(SELF_LINK);

        assertThrows(DataException.class,
                () -> service.update(accountingPolicies, transaction, COMPANY_ACCOUNTS_ID, request));
    }

    @Test
    @DisplayName("Tests the successful find of an AccountingPolicies resource")
    void findAccountingPolicies() throws DataException {

        when(repository.findById(RESOURCE_ID))
                .thenReturn(Optional.ofNullable(accountingPoliciesEntity));
        when(transformer.transform(accountingPoliciesEntity)).thenReturn(accountingPolicies);

        ResponseObject<AccountingPolicies> result = service.find(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(result);
        assertEquals(accountingPolicies, result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of an AccountingPolicies resource")
    void findAccountingPoliciesMongoException() {
        when(repository.findById(RESOURCE_ID)).thenThrow(mongoException);

        assertThrows(DataException.class, () -> service.find(COMPANY_ACCOUNTS_ID, request));
    }
}
