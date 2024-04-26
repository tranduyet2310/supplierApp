package com.example.suppileragrimart.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class ReviewStatisticResponse {
    @SerializedName("totalReviews")
    private long totalReviews;
    @SerializedName("averageRating")
    private BigDecimal averageRating;
    @SerializedName("oneStar")
    private Long oneStar;
    @SerializedName("twoStar")
    private long twoStar;
    @SerializedName("threeStar")
    private long threeStar;
    @SerializedName("fourStar")
    private long fourStar;
    @SerializedName("fiveStar")
    private long fiveStar;

    public ReviewStatisticResponse() {
    }

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Long getOneStar() {
        return oneStar;
    }

    public void setOneStar(Long oneStar) {
        this.oneStar = oneStar;
    }

    public long getTwoStar() {
        return twoStar;
    }

    public void setTwoStar(long twoStar) {
        this.twoStar = twoStar;
    }

    public long getThreeStar() {
        return threeStar;
    }

    public void setThreeStar(long threeStar) {
        this.threeStar = threeStar;
    }

    public long getFourStar() {
        return fourStar;
    }

    public void setFourStar(long fourStar) {
        this.fourStar = fourStar;
    }

    public long getFiveStar() {
        return fiveStar;
    }

    public void setFiveStar(long fiveStar) {
        this.fiveStar = fiveStar;
    }
}
