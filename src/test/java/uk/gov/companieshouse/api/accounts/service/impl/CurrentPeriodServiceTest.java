package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import java.security.MessageDigest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.repository.CurrentPeriodRepository;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CurrentPeriodTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CurrentPeriodServiceTest {


    @Mock
    private CurrentPeriod currentPeriod;

    @Mock
    private Transaction transaction;

    @Mock
    private CurrentPeriodRepository currentPeriodRepository;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private MessageDigest messageDigest;

    @Mock
    private CurrentPeriodEntity currentPeriodEntity;

    @Mock
    private CurrentPeriodTransformer currentPeriodTransformer;

    @Mock
    private DuplicateKeyException duplicateKeyException;

    @Mock
    private MongoException mongoException;

    @Mock
    private KeyIdGenerator keyIdGenerator;

    @InjectMocks
    private CurrentPeriodService currentPeriodService;

    public void setUpCreate() {
    }

    @Test
    @DisplayName("Tests the successful creation of a currentPeriod resource")
    public void canCreateCurrentPeriod() throws DataException {
        setUpCreate();
        when(currentPeriodTransformer.transform(currentPeriod)).thenReturn(currentPeriodEntity);
        ResponseObject<CurrentPeriod> result = currentPeriodService
                .create(currentPeriod, transaction, "", "");
        assertNotNull(result);
        assertEquals(currentPeriod, result.getData());
    }

    @Test
    @DisplayName("Tests the duplicate key when creating a current period resource")
    public void createSmallfullDuplicateKey() throws DataException {
        setUpCreate();
        doReturn(currentPeriodEntity).when(currentPeriodTransformer).transform(ArgumentMatchers
                .any(CurrentPeriod.class));
        when(currentPeriodRepository.insert(currentPeriodEntity)).thenThrow(duplicateKeyException);
        ResponseObject response = currentPeriodService.create(currentPeriod, transaction, "", "");
        assertNotNull(response);
        assertEquals(response.getStatus(), ResponseStatus.DUPLICATE_KEY_ERROR);
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Tests the mongo exception when creating a current period")
    void createSmallfullMongoExceptionFailure() throws DataException {
        setUpCreate();
        doReturn(currentPeriodEntity).when(currentPeriodTransformer).transform(ArgumentMatchers
                .any(CurrentPeriod.class));
        when(currentPeriodRepository.insert(currentPeriodEntity)).thenThrow(mongoException);
        Executable executable = () -> {
            currentPeriodService.create(currentPeriod, transaction, "", "");
        };
        assertThrows(DataException.class, executable);
    }

    @Test
    @DisplayName("Tests the successful find of a currentPeriod resource")
    public void findCurrentPeriod() throws DataException {
        when(currentPeriodRepository.findById(""))
                .thenReturn(Optional.ofNullable(currentPeriodEntity));
        when(currentPeriodTransformer.transform(currentPeriodEntity)).thenReturn(currentPeriod);
        ResponseObject<CurrentPeriod> result = currentPeriodService
                .findById("", "");
        assertNotNull(result);
        assertEquals(currentPeriod, result.getData());
    }

    @Test
    @DisplayName("Tests mongo exception thrown on find of a currentPeriod resource")
    public void findCurrentPeriodMongoException() throws DataException {
        when(currentPeriodRepository.findById("")).thenThrow(mongoException);
        Executable executable = () -> {
            currentPeriodService.findById("", "");
        };
        assertThrows(DataException.class, executable);
    }
}