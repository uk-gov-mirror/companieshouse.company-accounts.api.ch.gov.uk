package uk.gov.companieshouse.api.accounts.model.rest.notes.intangible;

import uk.gov.companieshouse.api.accounts.validation.CharSetValid;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntangibleAssets extends RestObject {

    private static final int MAX_FIELD_LENGTH = 20000;
    private static final int MIN_FIELD_LENGTH = 1;

    @Valid
    @JsonProperty("goodwill")
    private IntangibleAssetsResource goodWill;

    @Valid
    @JsonProperty("other_intangible_assets")
    private IntangibleAssetsResource other_intangible_assets;

    @Valid
    @JsonProperty("total")
    private IntangibleAssetsResource total;

    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message="invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("additional_information")
    private String additionalInformation;

    public IntangibleAssetsResource getGoodWill() {
        return goodWill;
    }

    public void setGoodWill(IntangibleAssetsResource goodWill) {
        this.goodWill = goodWill;
    }

    public IntangibleAssetsResource getOther_intangible_assets() {
        return other_intangible_assets;
    }

    public void setOther_intangible_assets(IntangibleAssetsResource other_intangible_assets) {
        this.other_intangible_assets = other_intangible_assets;
    }

    public IntangibleAssetsResource getTotal() {
        return total;
    }

    public void setTotal(IntangibleAssetsResource total) {
        this.total = total;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
