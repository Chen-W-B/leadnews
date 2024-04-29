package com.heima.wemedia.controller.v1;

import com.heima.model.admin.dtos.ChannelDto;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.service.WmChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/channel")
@Api(tags = "频道信息相关接口")
public class WmChannelController {
    @Autowired
    private WmChannelService wmChannelService;

    @GetMapping("/channels")
    @ApiOperation("查询所有频道")
    public ResponseResult findAll(){
        return wmChannelService.findAll();
    }

    @PostMapping("/save")
    @ApiOperation("保存频道")
    public ResponseResult save(@RequestBody AdChannel channel){
        return wmChannelService.save(channel);
    }

    @PostMapping("/list")
    @ApiOperation("模糊匹配分页查询")
    public ResponseResult findListWithPage(@RequestBody ChannelDto dto){
        return wmChannelService.findListWithPage(dto);
    }

    @PostMapping("/update")
    @ApiOperation("更新频道")
    public ResponseResult update(@RequestBody AdChannel channel){
        return wmChannelService.updateChannel(channel);
    }

    @GetMapping("/del/{id}")
    @ApiOperation("删除频道")
    public ResponseResult delete(@PathVariable Integer id){
        return wmChannelService.deleteChannel(id);
    }

}
