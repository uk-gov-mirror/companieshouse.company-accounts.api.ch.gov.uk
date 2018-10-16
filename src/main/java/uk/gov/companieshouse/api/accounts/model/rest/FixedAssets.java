package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

public class FixedAssets {

    private static final int MAX_RANGE = 99999999;
    private static final int MIN_RANGE = 0;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("tangible")
    private Long tangible;

    @JsonProperty("total")
    private Long totalFixedAssets;

    public Long getTangible() {
        return tangible;
    }

    public void setTangible(Long tangible) {
        this.tangible = tangible;
    }

    public Long getTotalFixedAssets() {
        return totalFixedAssets;
    }

    public void setTotalFixedAssets(Long totalFixedAssets) {
        this.totalFixedAssets = totalFixedAssets;
    }
}
