package com.heima.wemedia.controller.v1;

import com.heima.common.constants.WemediaConstants;
import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
@Api(tags = "自媒体图文内容信息相关接口")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    //查询所有文章
    @PostMapping("/list")
    @ApiOperation("查询所有文章")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto dto){
        return wmNewsService.findAll(dto);
    }

    //新增文章
    @PostMapping("/submit")
    @ApiOperation("新增文章")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto){
        return wmNewsService.submitNews(dto);
    }

    //文章上下架
    @PostMapping("/down_or_up")
    @ApiOperation("文章上下架")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto){
        return wmNewsService.downOrUp(dto);
    }


    @PostMapping("/list_vo")
    @ApiOperation("查询文章列表")
    public ResponseResult findList(@RequestBody NewsAuthDto dto){
        return wmNewsService.findList(dto);
    }

    @GetMapping("/one_vo/{id}")
    @ApiOperation("查询文章详情")
    public ResponseResult findWmNewsVo(@PathVariable("id") Integer id){
        return wmNewsService.findWmNewsVo(id);
    }

    @PostMapping("/auth_pass")
    @ApiOperation("文章上下架")
    public ResponseResult authPass(@RequestBody NewsAuthDto dto){
        return wmNewsService.updateStatus(WemediaConstants.WM_NEWS_AUTH_PASS,dto);
    }

    @PostMapping("/auth_fail")
    @ApiOperation("审核成功")
    public ResponseResult authFail(@RequestBody NewsAuthDto dto){
        return wmNewsService.updateStatus(WemediaConstants.WM_NEWS_AUTH_FAIL,dto);
    }
}
