package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleCommentDto;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.mess.ArticleVisitStreamMess;
import com.heima.model.wemedia.dtos.StatisticsDto;

import java.util.Date;

public interface ApArticleService extends IService<ApArticle> {
    /**
     * 根据参数加载文章列表
     * @param loadtype 1为加载更多  2为加载最新
     * @param dto
     * @return
     */
    public ResponseResult load(Short loadtype, ArticleHomeDto dto);

    /**
     * 根据参数加载文章列表
     * @param loadtype 1为加载更多  2为加载最新
     * @param dto
     * @param firstPage  true是首页，false是 非首页
     * @return
     */
    public ResponseResult load2(Short loadtype, ArticleHomeDto dto, boolean firstPage);

    public ResponseResult saveArticle(ArticleDto dto);//保存app端相关文章



    /**
     * 加载文章详情 数据回显
     * @param dto
     * @return
     */
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto);

    /**
     * 更新文章的分值  同时更新缓存中的热点文章数据
     * @param mess
     */
    public void updateScore(ArticleVisitStreamMess mess);

    /**
     * 图文统计统计
     * @param wmUserId
     * @param beginDate
     * @param endDate
     * @return
     */
    public ResponseResult queryLikesAndConllections(Integer wmUserId, Date beginDate, Date endDate);

    /**
     * 分页查询 图文统计
     * @param dto
     * @return
     */
    public PageResponseResult newPage(StatisticsDto dto);

    /**
     * 查询文章评论统计
     * @param dto
     * @return
     */
    public PageResponseResult findNewsComments(ArticleCommentDto dto);
}
