package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;

public interface WmChannelService extends IService<WmChannel> {

    public ResponseResult findAll();//查询所有频道

    public ResponseResult save(AdChannel channel);//保存频道

    public ResponseResult findListWithPage(ChannelDto dto);//模糊匹配分页查询

    public ResponseResult updateChannel(AdChannel channel);//更新频道

    public ResponseResult deleteChannel(Integer id);//删除频道

}