package uk.gov.companieshouse.api.accounts.service;

import java.security.NoSuchAlgorithmException;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;

public interface AbstractService<C extends RestObject, E extends BaseEntity> {

    ResponseObject<C> save(C rest, String companyAccountId);

    E findById(String id);

    void addEtag(C rest);

    void addLinks(C rest);

    void addKind(C rest);

    String getResourceName();

    String generateID(String value) throws NoSuchAlgorithmException;
}