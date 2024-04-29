package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

public interface ApAssociateWordsService {

    /**
     * 关键字搜索联想词
     * @param dto
     * @return
     */
    public ResponseResult search(UserSearchDto dto);
}
