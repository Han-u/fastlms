package com.zb.fastlms.admin.model;

import lombok.Data;

@Data
public class MemberParam {
    long pageIndex;
    long pageSize;

    String searchType;
    String searchValue;

    public long getPageStart(){
        if(pageIndex < 1){
            pageIndex = 1;
        }

        if(pageSize < 10){
            pageSize = 10;
        }

        return (pageIndex - 1) * pageSize;
    }

    public long getPageEnd(){
        if (pageSize < 10){
            pageSize = 10;
        }
        return pageSize;
    }

    public void init(){
        if(pageIndex < 1){
            pageIndex = 1;
        }
        if(pageSize < 10){
            pageSize = 10;
        }
    }

    public String getQueryString() {
        init();
        StringBuilder sb = new StringBuilder();
        if(searchType != null && searchType.length() > 0){
            sb.append(String.format("searchType=%s", searchType));
        }
        if(searchType != null && searchType.length() > 0){
            if(sb.length() > 0){
                sb.append("&");
            }
            sb.append(String.format("searchValue=%s", searchValue));
        }
        return sb.toString();
    }
}
