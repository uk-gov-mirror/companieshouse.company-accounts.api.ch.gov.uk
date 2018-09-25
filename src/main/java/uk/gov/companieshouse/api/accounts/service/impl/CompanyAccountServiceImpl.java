package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.repository.CompanyAccountRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.ApiErrorResponseException;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionManager;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyAccountServiceImpl implements CompanyAccountService {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);
    @Autowired
    private TransactionManager transactionManager;
    @Autowired
    private CompanyAccountRepository companyAccountRepository;
    @Autowired
    private CompanyAccountTransformer companyAccountTransformer;

    /**
     * {@inheritDoc}
     */
    public ResponseObject<CompanyAccount> create(CompanyAccount companyAccount,
        Transaction transaction, String requestId)
        throws PatchException, DataException {

        String id = generateID();
        String companyAccountLink = createSelfLink(transaction, id);
        companyAccount.setKind(Kind.COMPANY_ACCOUNTS.getValue());
        addEtag(companyAccount);
        addLinks(companyAccount, companyAccountLink);

        CompanyAccountEntity companyAccountEntity = companyAccountTransformer
            .transform(companyAccount);

        companyAccountEntity.setId(id);

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("company_accounts_id", id);

        try {
            companyAccountRepository.insert(companyAccountEntity);
        } catch (DuplicateKeyException dke) {
            LOGGER.errorContext(requestId, dke, debugMap);
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR, null);
        } catch (MongoException me) {
            DataException dataException = new DataException(
                "Failed to insert company account entity", me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }

        try {
            transactionManager
                .updateTransaction(transaction.getId(), requestId, companyAccountLink);
        } catch (ApiErrorResponseException aere) {
            PatchException patchException = new PatchException(
                "Failed to patch transaction", aere);
            LOGGER.errorContext(requestId, patchException, debugMap);
            throw patchException;
        }

        return new ResponseObject<>(ResponseStatus.CREATED, companyAccount);
    }

    @Override
    public void addLink(String id, LinkType linkType, String link) {
        CompanyAccountEntity companyAccountEntity = companyAccountRepository.findById(id)
            .orElseThrow(() -> new MongoException(
                "Failed to add link to Company account entity"));
        CompanyAccountDataEntity companyAccountDataEntity = companyAccountEntity.getData();
        Map<String, String> map = companyAccountDataEntity.getLinks();
        map.put(linkType.getLink(), link);
        companyAccountDataEntity.setLinks(map);
        companyAccountRepository.save(companyAccountEntity);
    }

    private void addLinks(CompanyAccount companyAccount, String companyAccountLink) {
        Map<String, String> map = new HashMap<>();
        map.put(LinkType.SELF.getLink(), companyAccountLink);
        companyAccount.setLinks(map);
    }

    private String createSelfLink(Transaction transaction, String id) {
        return getTransactionSelfLink(transaction) + "/" + ResourceName.COMPANY_ACCOUNT.getName()
            + "/" + id;
    }

    private String getTransactionSelfLink(Transaction transaction) {
        return transaction.getLinks().get(LinkType.SELF.getLink());
    }

    private void addEtag(CompanyAccount rest) {
        rest.setEtag(GenerateEtagUtil.generateEtag());
    }

    public String generateID() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    public ResponseObject<CompanyAccount> findById(String id, String requestId)
        throws DataException {

        CompanyAccountEntity companyAccountEntity;
        try {
            companyAccountEntity = companyAccountRepository.findById(id).orElse(null);
        } catch (MongoException me) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("id", id);
            DataException dataException = new DataException("Failed to find Company Account", me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }

        if (companyAccountEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }
        CompanyAccount companyAccount = companyAccountTransformer.transform(companyAccountEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, companyAccount);
    }
}