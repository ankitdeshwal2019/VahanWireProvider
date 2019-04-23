
package com.electrom.vahanwireprovider.models.quote_status;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuoteList {

    @SerializedName("quote_image_link")
    @Expose
    private String quoteImageLink;
    @SerializedName("quote_time")
    @Expose
    private String quoteTime;
    @SerializedName("quote_status")
    @Expose
    private Integer quoteStatus;

    public String getQuoteImageLink() {
        return quoteImageLink;
    }

    public void setQuoteImageLink(String quoteImageLink) {
        this.quoteImageLink = quoteImageLink;
    }

    public String getQuoteTime() {
        return quoteTime;
    }

    public void setQuoteTime(String quoteTime) {
        this.quoteTime = quoteTime;
    }

    public Integer getQuoteStatus() {
        return quoteStatus;
    }

    public void setQuoteStatus(Integer quoteStatus) {
        this.quoteStatus = quoteStatus;
    }

}
