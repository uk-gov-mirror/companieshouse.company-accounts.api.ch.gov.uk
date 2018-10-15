package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.links.TransactionLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.repository.PreviousPeriodRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.PreviousPeriodTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class PreviousPeriodService implements ResourceService<PreviousPeriod> {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private PreviousPeriodRepository previousPeriodRepository;

    private PreviousPeriodTransformer previousPeriodTransformer;

    private SmallFullService smallFullService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public PreviousPeriodService(
        PreviousPeriodRepository previousPeriodRepository,
        PreviousPeriodTransformer previousPeriodTransformer,
        SmallFullService smallFullService,
        KeyIdGenerator keyIdGenerator) {
        this.previousPeriodRepository = previousPeriodRepository;
        this.previousPeriodTransformer = previousPeriodTransformer;
        this.smallFullService = smallFullService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<PreviousPeriod> create(PreviousPeriod previousPeriod,
        Transaction transaction, String companyAccountId, String requestId) throws DataException {

        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(previousPeriod, selfLink);
        previousPeriod.setEtag(GenerateEtagUtil.generateEtag());
        previousPeriod.setKind(Kind.PREVIOUS_PERIOD.getValue());
        PreviousPeriodEntity previousPeriodEntity = previousPeriodTransformer
            .transform(previousPeriod);

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("company_accounts_id", companyAccountId);

        String id = generateID(companyAccountId);
        previousPeriodEntity.setId(id);
        debugMap.put("id", id);

        try {
            previousPeriodRepository.insert(previousPeriodEntity);
        } catch (DuplicateKeyException dke) {
            LOGGER.errorContext(requestId, dke, debugMap);
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR, null);
        } catch (MongoException me) {
            DataException dataException = new DataException(
                "Failed to insert " + ResourceName.PREVIOUS_PERIOD.getName(), me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }

        smallFullService
            .addLink(companyAccountId, SmallFullLinkType.PREVIOUS_PERIOD, selfLink, requestId);

        return new ResponseObject<>(ResponseStatus.CREATED, previousPeriod);
    }

    @Override
    public ResponseObject<PreviousPeriod> findById(String id, String requestId) throws DataException {
        PreviousPeriodEntity previousPeriodEntity;
        try {
            previousPeriodEntity = previousPeriodRepository.findById(id).orElse(null);
        } catch (MongoException me) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("id", id);
            DataException dataException = new DataException("Failed to find Previous Period", me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }

        if (previousPeriodEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }
        PreviousPeriod previousPeriod = previousPeriodTransformer.transform(previousPeriodEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, previousPeriod);
    }

    public ResponseObject<PreviousPeriod> update(PreviousPeriod rest, Transaction transaction,
        String companyAccountId, String requestId) throws DataException {
        //TODO implement method
        return null;
    }


    @Override
    public String generateID(String value) {
        return keyIdGenerator.generate(value + "-" + ResourceName.PREVIOUS_PERIOD.getName());
    }

    public String createSelfLink(Transaction transaction, String companyAccountId) {

        return buildSelfLink(transaction, companyAccountId);
    }

    private String buildSelfLink(Transaction transaction, String companyAccountId) {

        StringBuilder builder = new StringBuilder();
        builder.append(transaction.getLinks().get(TransactionLinkType.SELF.getLink())).append("/")
            .append(ResourceName.COMPANY_ACCOUNT.getName()).append("/")
            .append(companyAccountId).append("/")
            .append(ResourceName.SMALL_FULL.getName()).append("/")
            .append(ResourceName.PREVIOUS_PERIOD.getName());

        return builder.toString();
    }

    private void initLinks(PreviousPeriod previousPeriod, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), link);
        previousPeriod.setLinks(map);
    }
}