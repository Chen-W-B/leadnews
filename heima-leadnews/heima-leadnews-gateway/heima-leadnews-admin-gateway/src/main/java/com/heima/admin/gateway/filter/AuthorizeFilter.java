package com.heima.admin.gateway.filter;

import com.heima.admin.gateway.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //2.判断是否是登录
        if (request.getURI().getPath().contains("/login")){
            //放行 -- 如果是登录，则路由到后台管理微服务进行登录
            return chain.filter(exchange);
        }

        //3.获取token
        String token = request.getHeaders().getFirst("token");

        //4.判断token是否存在
        if (StringUtils.isBlank(token)){
            //token不存在
            response.setStatusCode(HttpStatus.UNAUTHORIZED);//校验失败，编码为401
            return response.setComplete();//结束请求，并返回编码401
        }

        //5.判断token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            //是否是过期
            int result = AppJwtUtil.verifyToken(claimsBody);//-1：有效，0：有效，1：过期，2：过期
            if (result == 1 || result == 2){
                //token已过期
                response.setStatusCode(HttpStatus.UNAUTHORIZED);//校验失败，编码为401
                return response.setComplete();//结束请求，并返回编码401
            }

            //获取用户信息
            Object userId = claimsBody.get("id");
            //存储到header中
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("userId", userId + "");
            }).build();
            //重置请求
            exchange.mutate().request(serverHttpRequest);

        }catch (Exception e){
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);//校验失败，编码为401
            return response.setComplete();//结束请求，并返回编码401
        }

        //6.放行
        return chain.filter(exchange);
    }


    /**
     * 优先级设置  值越小  优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
