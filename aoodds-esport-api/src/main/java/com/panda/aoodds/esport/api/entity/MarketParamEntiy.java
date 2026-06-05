package com.panda.aoodds.esport.api.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Properties;

/**
 * @Author carson
 * @DATE 2022/3/9 14:29
 **/
@Data
public class MarketParamEntiy implements java.io.Serializable,Cloneable {

    private String id;
    private String aoMatchId;
    private String ftge;
    private String htge;
    private String ftsup;
    private String htsup;

    private String originalftge;
    private String originalhtge;
    private String originalftsup;
    private String originalhtsup;


    private String ftdrawadj;//概率值修正
    private String htdrawadj;
    private String half1stPeriod;//阶段时长
    private String injTime1st; //上半场伤停补时
    private String injTime2nd; //下半场伤停补时
    private String refresh;//赔率刷新频率
    private String dis0to151H;//每15分钟修正值
    private String dis15to301H;
    private String dis45to602H;
    private String dis60to752H;

    private String htAhHandicap;//半场让球盘口
    private Double htAhOdds1;//半场让球主队赔率
    private Double htAhOdds2;//半场让球客队赔率
    private String htOuHandicap;//半场大小盘口
    private Double htOuOdds1;//半场大小 大 赔率
    private Double htOuOdds2;//半场大小 小 赔率
    private String ftAhHandicap;
    private Double ftAhOdds1;
    private Double ftAhOdds2;
    private String ftOuHandicap;
    private Double ftOuOdds1;
    private Double ftOuOdds2;

    private String mins0to15 = "0:0";//比分
    private String mins15to30 = "0:0";
    private String mins30toht = "0:0";
    private String minshtto60 = "0:0";
    private String mins60to75 = "0:0";
    private String mins75toft = "0:0";
    public String matchClock="0";//当前赛事进行时长
    public String half1stOr2nd;//阶段 将标准阶段6、7转为1、2

    private String isManualSocre = "0";//是否手工比分 0否：1是
    private String periodTimer;//赛事进行时间修正
    private Integer period;//修正的阶段

    private String isEventTime;
    //----------点球大战参数----------
    /**
     * 哪队先踢点球
     */
    private String firstKick;
    /**
     * 当前已经踢了几颗点球
     */
    private Integer pointNum;
    /**
     * 比分详情
     */
    private String score;
    /**
     * 每轮赔率详情
     */
    private String forecastValue;
    /**
     * 常规赛平局后是否没有加时直接进入点球，0-不是，1-是
     */
    public Integer without = 0;
    /**
     * 点球大战特殊玩法标识
     */
    private boolean specPk;
    /**
     * 常规时间独赢主胜概率
     */
    private Double ft1x2HomeProb;
    /**
     * 常规时间独赢客胜概率
     */
    private Double ft1x2AwayProb;
    /**
     * 常规时间独赢和局概率
     */
    private Double ft1x2DrawProb;
    /**
     * 加时独赢主胜概率
     */
    private Double et1x2HomeProb;
    /**
     * 加时独赢客胜概率
     */
    private Double et1x2AwayProb;
    /**
     * 加时独赢和局概率
     */
    private Double et1x2DrawProb;

    /**
     * 角球，罚牌，进球类
     */
    private String requestType;

    private String autoRev="0";
    private String autoRevValue="1";//取值1、10、20、30
    private String autoApply="0";
    /**
     * 1勾选 0未勾选，new
     */
    private String newVersion="0";
    /**
     * 当前使用那个版本，双版本部署
     */
    public String lineVersion;
    public String linkId;
    /****特殊事件 主客队赔率***updateHASE：0(未激活)，1（主队激活），2（客队激活） **/
    Integer updateHASE=0;
    String updateHASEProb;
    /**
     * 二次确认
     */
    String difference;
    /**
     * 使用哪个数据源进行rev
     */
    String dataSourceCode;
    Integer matchUiStatus;
    Long modifyTime=System.currentTimeMillis();

    public Properties toProperties() {
        return JSON.parseObject(JSON.toJSONString(this), Properties.class);
    }

    public void setFtge(String ftge) {
        this.ftge = new BigDecimal(ftge).setScale(3, BigDecimal.ROUND_DOWN).toString();
    }

    public void setHtge(String htge) {
        this.htge = new BigDecimal(htge).setScale(3, BigDecimal.ROUND_DOWN).toString();
    }

    public void setFtsup(String ftsup) {
        this.ftsup = new BigDecimal(ftsup).setScale(3, BigDecimal.ROUND_DOWN).toString();
    }

    public void setHtsup(String htsup) {
        this.htsup = new BigDecimal(htsup).setScale(3, BigDecimal.ROUND_DOWN).toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MarketParamEntiy marketParamEntiy = null;
        try{
            marketParamEntiy = (MarketParamEntiy)super.clone(); //克隆后需要转型
        }catch(CloneNotSupportedException e) { //此处需要捕捉异常
            e.printStackTrace();
        }
        return marketParamEntiy;
    }
}
