package com.example.suppileragrimart.model;

import static com.example.suppileragrimart.utils.Constants.DEFAULT_PAGE_NUMBER;
import static com.example.suppileragrimart.utils.Constants.DEFAULT_PAGE_SIZE;
import static com.example.suppileragrimart.utils.Constants.DEFAULT_SORT_BY;
import static com.example.suppileragrimart.utils.Constants.DEFAULT_SORT_DIRECTION;

public class SearchApiRequest {
    private String query;
    private String pageNo = DEFAULT_PAGE_NUMBER;
    private String pageSize = DEFAULT_PAGE_SIZE;
    private String sortBy = DEFAULT_SORT_BY;
    private String sortDir = DEFAULT_SORT_DIRECTION;

    public SearchApiRequest() {
    }

    public SearchApiRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}
