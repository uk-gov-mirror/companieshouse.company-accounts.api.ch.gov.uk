package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

@JsonInclude(Include.NON_NULL)
public class Approval extends RestObject {

    @NotNull
    @JsonProperty("date")
    @PastOrPresent(message="PAST_OR_PRESENT_DATE")
    private LocalDate date;

    @NotNull
    @JsonProperty("name")
    @CharSetValid(CharSet.CHARECTER_SET_2)
    private String name;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Approval{" +
            "date=" + date +
            ", name='" + name + '\'' +
            '}';
    }
}