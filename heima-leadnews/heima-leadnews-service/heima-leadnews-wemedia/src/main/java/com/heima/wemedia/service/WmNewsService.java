package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

public interface WmNewsService extends IService<WmNews> {

    public ResponseResult findAll(WmNewsPageReqDto dto);//查询文章

    public ResponseResult submitNews(WmNewsDto dto);//发布文章或保存草稿

    public ResponseResult downOrUp(WmNewsDto dto);//文章上下架



    public ResponseResult findList(NewsAuthDto dto);//查询文章列表

    public ResponseResult findWmNewsVo(Integer id);//查询文章详情

    /**
     * 文章审核，修改状态
     * @param status  2  审核失败  4 审核成功
     * @param dto
     * @return
     */
    public ResponseResult updateStatus(Short status ,NewsAuthDto dto);
}
