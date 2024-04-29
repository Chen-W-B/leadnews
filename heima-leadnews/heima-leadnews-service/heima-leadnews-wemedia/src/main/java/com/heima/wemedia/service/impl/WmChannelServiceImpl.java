package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@Slf4j
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {


    /**
     * 查询所有频道
     * @return
     * */
    @Override
    public ResponseResult findAll() {
        //list()是mybatis plus自带的查询所有数据的方法
        return ResponseResult.okResult(list());
    }

    /**
     * 保存频道
     * @param channel
     * @return
     */
    @Override
    public ResponseResult save(AdChannel channel) {
        //1.检查参数
        if (channel == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmChannel wmChannel = getOne(Wrappers.<WmChannel>lambdaQuery().eq(WmChannel::getName, channel.getName()));
        if (wmChannel != null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST, "频道已存在");
        }

        //2.保存
        wmChannel = new WmChannel();
        BeanUtils.copyProperties(channel, wmChannel);
        wmChannel.setCreatedTime(new Date());
        save(wmChannel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 模糊匹配分页查询
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findListWithPage(ChannelDto dto) {
        //1.参数检查
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.分页查询
        IPage pageQuery = new Page(dto.getPage(),dto.getSize());
        //3.按照不同需求查询
        LambdaQueryWrapper<WmChannel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //3.1 关键字模糊匹配
        if (StringUtils.isNotBlank(dto.getName())){
            lambdaQueryWrapper.like(WmChannel::getName, dto.getName());
        }
        //3.2 排序
        lambdaQueryWrapper.orderByDesc(WmChannel::getCreatedTime);
        pageQuery = page(pageQuery, lambdaQueryWrapper);
        //4.返回结果
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) pageQuery.getTotal());
        responseResult.setData(pageQuery.getRecords());
        return responseResult;
    }

    @Autowired
    private WmNewsService wmNewsService;

    /**
     * 更新频道
     * @param channel
     * @return
     */
    @Override
    public ResponseResult updateChannel(AdChannel channel) {
        // 1.参数检查
        if(channel == null || channel.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.判断是否被引用
        int count = wmNewsService.count(Wrappers.<WmNews>lambdaQuery().eq(WmNews::getChannelId, channel.getId())
                .eq(WmNews::getStatus, WmNews.Status.PUBLISHED.getCode()));
        if (count > 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"频道被引用不能修改或禁用");
        }

        // 3.更新
        WmChannel wmChannel = new WmChannel();
        BeanUtils.copyProperties(channel,wmChannel);
        updateById(wmChannel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 删除频道
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteChannel(Integer id) {
        //1.检查参数
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.查询频道
        WmChannel wmChannel = getById(id);
        if(wmChannel == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        //3.频道是否有效
        if(wmChannel.getStatus()){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"频道有效，不能删除");
        }

        //判断是否被引用
        int count = wmNewsService.count(Wrappers.<WmNews>lambdaQuery().eq(WmNews::getChannelId, wmChannel.getId())
                .eq(WmNews::getStatus, WmNews.Status.PUBLISHED.getCode()));
        if(count > 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"频道被引用不能删除");
        }

        //4.删除
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}