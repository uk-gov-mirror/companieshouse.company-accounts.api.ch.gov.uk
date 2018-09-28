package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodTransformerTest {

    private static final Integer CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID = 5;
    public static final String ETAG = "etag";
    public static final String KIND = "kind";

    private PreviousPeriodTransformer previousPeriodTransformer = new PreviousPeriodTransformer();

    @Test
    @DisplayName("Tests previous period transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {
        PreviousPeriodEntity companyAccountEntity = previousPeriodTransformer
            .transform(new PreviousPeriod());

        assertNotNull(companyAccountEntity);
        assertNull(companyAccountEntity.getData().getEtag());
        assertEquals(new HashMap<>(), companyAccountEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests previous period transformer with populated object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setEtag(ETAG);
        previousPeriod.setKind(KIND);
        previousPeriod.setLinks(new HashMap<>());
        previousPeriod.setBalanceSheet(balanceSheet);

        PreviousPeriodEntity previousPeriodEntity = previousPeriodTransformer
            .transform(previousPeriod);

        PreviousPeriodDataEntity data = previousPeriodEntity.getData();

        assertNotNull(previousPeriodEntity);
        assertEquals(ETAG, data.getEtag());
        assertEquals(CALLED_UP_SHARE_CAPITAL_NOT_PAID_VALID,
            data.getBalanceSheetEntity().getCalledUpShareCapitalNotPaid());
        assertEquals(KIND, data.getKind());
        assertEquals(new HashMap<>(), data.getLinks());
    }
}
