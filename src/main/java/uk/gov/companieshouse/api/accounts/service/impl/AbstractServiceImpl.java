package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.AbstractService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.GenericTransformer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public abstract class AbstractServiceImpl<T extends RestObject, U extends BaseEntity, V extends BaseEntity> implements
        AbstractService<T, U, V> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private MongoRepository<U, String> mongoRepository;

    private GenericTransformer<T, U> genericTransformer;

    private MongoRepository<V, String> parentMongoRepository;

    private MessageDigest messageDigest;

    public AbstractServiceImpl(MongoRepository<U, String> mongoRepository,
            GenericTransformer<T, U> genericTransformer,
            MongoRepository<V, String> parentMongoRepository) {
        this.mongoRepository = mongoRepository;
        this.genericTransformer = genericTransformer;
        this.parentMongoRepository = parentMongoRepository;
    }

    @Override
    public ResponseObject<T> create(T rest, Transaction transaction, String companyAccountId,
            String requestId)
            throws DataException {

        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(rest, selfLink);
        rest.setEtag(GenerateEtagUtil.generateEtag());
        addKind(rest);
        U baseEntity = genericTransformer.transform(rest);

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("company_accounts_id", companyAccountId);

        try {
            baseEntity.setId(generateID(companyAccountId, getResourceName()));
            mongoRepository.insert(baseEntity);
            addParentLink(companyAccountId, selfLink);
        } catch (DuplicateKeyException dke) {
            LOGGER.errorContext(requestId, dke, debugMap);
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR, null);
        } catch (MongoException me) {
            DataException dataException = new DataException(
                    "Failed to insert " + getResourceName(), me);
            LOGGER.errorContext(requestId, dataException, debugMap);
            throw dataException;
        }

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<T> findById(String id) {
        U entity = mongoRepository.findById(id).orElse(null);
        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }
        T rest = genericTransformer.transform(entity);
        return new ResponseObject<>(ResponseStatus.FOUND, rest);
    }

    @Override
    public String generateID(String value, String resourceName) {
        String unencryptedId = value + "-" + resourceName;
        byte[] id = messageDigest.digest(
                unencryptedId.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(id);
    }

    @Override
    public void initLinks(T rest, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(LinkType.SELF.getLink(), link);
        rest.setLinks(map);
    }

    @Autowired
    public void setMessageDigest(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }

    public MongoRepository<V, String> getParentMongoRepository() {
        return parentMongoRepository;
    }
}