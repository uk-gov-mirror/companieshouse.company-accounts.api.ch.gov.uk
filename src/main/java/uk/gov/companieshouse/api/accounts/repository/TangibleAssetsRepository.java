package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsEntity;

@Repository
public interface TangibleAssetsRepository extends MongoRepository<TangibleAssetsEntity, String>{

}
