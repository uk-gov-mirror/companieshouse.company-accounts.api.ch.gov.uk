package uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity;

import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "notes")
public class CreditorsAfterOneYearEntity extends BaseEntity {

    private CreditorsAfterOneYearDataEntity data;

    public CreditorsAfterOneYearDataEntity getData() {
        return data;
    }

    public void setData(CreditorsAfterOneYearDataEntity data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (! (o instanceof CreditorsAfterOneYearEntity))
            return false;
        CreditorsAfterOneYearEntity that = (CreditorsAfterOneYearEntity) o;
        return Objects.equals(getData(), that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getData());
    }

    @Override
    public String toString() {
        return "CreditorsAfterOneYearEntity{" +
                "data=" + data +
                '}';
    }
}