package com.panda.aoodds.esport.handle;

import com.panda.aoodds.esport.common.exception.ApiException;
import com.panda.sports.algo.api.service.C01CalculateService;
import com.panda.sports.algo.api.service.CashOutService;
import com.panda.sports.algo.api.service.MarketsAssembleService;
import com.panda.sports.algo.api.service.ReverseParamService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @Author carson
 * @DATE 2022/4/12 19:10
 **/
@Component
@RefreshScope
public class MarketLoadBalanceHandler {
    @DubboReference(check = false)
    private MarketsAssembleService marketsAssembleService;
    @DubboReference
    CashOutService cashOutService;
    @DubboReference
    private ReverseParamService reverseParamService;
    @DubboReference
    C01CalculateService c01CalculateService;
    /**
     * 赔率主盘口下发数量
     */
    @Value("${ao.goalMainMarketCount}")
    private Integer goalMainMarketCount;

    public String  getMarkets(Properties var1, String var2, String var3){

        if("100".equals(var1.getProperty("half1stOr2nd"))&&var1.getProperty("requestType").contains("g_")){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("110".equals(var1.getProperty("half1stOr2nd"))&&var1.getProperty("requestType").contains("ex_")){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("120".equals(var1.getProperty("half1stOr2nd"))&&var1.getProperty("requestType").contains("pk")){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("999".equals(var1.getProperty("half1stOr2nd"))){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        var1.put("goalMainMarketCount", goalMainMarketCount);
      //  String marketsVersion=var1.getProperty("lineVersion","es");
        RpcContext.getContext().setAttachment("dubbo.tag","es");
       return c01CalculateService.getMarkets(var1,var3);
    }
    /********获取滚球计算参数**********/
    public String  getFBCaclMarketParam(Properties var1, String var2){

        if("100".equals(var1.getProperty("half1stOr2nd"))&&var1.getProperty("requestType").contains("g_")){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("110".equals(var1.getProperty("half1stOr2nd"))&&var1.getProperty("requestType").contains("ex_")){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("120".equals(var1.getProperty("half1stOr2nd"))&&var1.getProperty("requestType").contains("pk")){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("999".equals(var1.getProperty("half1stOr2nd"))){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }

        RpcContext.getContext().setAttachment("dubbo.tag","es");
        return marketsAssembleService.getFBCaclMarketParam(var1,var2);
    }
    public String getEsportMarkets(Properties var1, String var2, String var3){
        if("100".equals(var1.getProperty("half1stOr2nd"))){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("110".equals(var1.getProperty("half1stOr2nd"))){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("120".equals(var1.getProperty("half1stOr2nd"))){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("999".equals(var1.getProperty("half1stOr2nd"))){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        var1.put("goalMainMarketCount", goalMainMarketCount);
        RpcContext.getContext().setAttachment("dubbo.tag","es");
        var1.put("half1stPeriod","45");
        return c01CalculateService.getMarkets(var1,var2);
    }
    /**
     *
     * @param var1
     * @param var2
     * @param var3
     * @return
     */
  /*  public String  getMarketsCashOut(Properties var1, String var2, String var3){

        if("100".equals(var1.getProperty("half1stOr2nd"))&&var1.getProperty("requestType").contains("g_")){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("110".equals(var1.getProperty("half1stOr2nd"))&&var1.getProperty("requestType").contains("ex_")){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("120".equals(var1.getProperty("half1stOr2nd"))&&var1.getProperty("requestType").contains("pk")){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        if("999".equals(var1.getProperty("half1stOr2nd"))){
            throw new ApiException("赛事阶段性结束！"+var1.getProperty("half1stOr2nd"));
        }
        var1.put("goalMainMarketCount", goalMainMarketCount);
        RpcContext.getContext().setAttachment("dubbo.tag","es");
        return cashOutService.getCashOutProbabilities(var1,var2);
    }*/
    public String reverseParam(Properties var1, String var2, String var3){
        String marketsVersion=var1.getProperty("lineVersion","red");
        RpcContext.getContext().setAttachment("dubbo.tag",marketsVersion);
        return reverseParamService.reverseParam(var1,var2,var3);
    }
}
