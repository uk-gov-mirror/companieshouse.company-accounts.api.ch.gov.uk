package uk.gov.companieshouse.api.accounts.model.entity;

import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Field;

public class BalanceSheetEntity {

    @NotNull
    @Field("called_up_share_capital_not_paid")
    private Integer calledUpShareCapitalNotPaid;

    public Integer getCalledUpShareCapitalNotPaid() {
        return calledUpShareCapitalNotPaid;
    }

    public void setCalledUpShareCapitalNotPaid(Integer calledUpShareCapitalNotPaid) {
        this.calledUpShareCapitalNotPaid = calledUpShareCapitalNotPaid;
    }
}