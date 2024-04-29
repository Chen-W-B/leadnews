package com.heima.model.article.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleHomeDto {

    // 最大时间 默认参数：0（毫秒）
    public Date maxBehotTime;
    // 最小时间 默认参数：20000000000000（毫秒）--->2063年
    public Date minBehotTime;
    // 分页size
    public Integer size;
    // 频道ID
    public String tag;
}