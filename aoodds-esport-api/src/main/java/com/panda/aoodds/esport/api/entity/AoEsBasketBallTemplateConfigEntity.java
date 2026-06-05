package com.panda.aoodds.esport.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author: jstyChandler
 * @Package: com.panda.sport.rcs.trade.vo.tourTemplate
 * @ClassName: AoBasketBallTemplateConfigEntity
 * @Description: TODO
 * @Date: 2023/2/6 13:21
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class AoEsBasketBallTemplateConfigEntity implements java.io.Serializable {
    String id;
    /**
     * Default of SD as S2
     */
    Integer useOuSd;
    Integer refresh;
    Integer closingGe;
    Integer closingTime;
    Integer overtime;
    /**
     * Int Handicap Lines
     */
    Integer handicapModel;
    /**
     * AO赛事id
     **/
    String aoMatchId;
    /**
     * 标准赛事ID
     **/
    String standardMatchId;
    /**
     * 联赛等级
     **/
    Integer tournamentLevel;
    Integer sportId;
    Double xgdHT;
    Double xgdQ1;
    Double xgdQ3;
    Integer matchType;
    @JSONField(name="S1Value")
    Integer s1Value=0;
    @JSONField(name="S2Value")
    Integer s2Value=0;
    @JSONField(name="S3Value")
    Double s3Value=0.0;
    @JSONField(name="S4Value")
    Double s4Value=0.0;
    Float segment0;
    Float segment1;
    Float segment2;
    Float segment3;
    Float segmentOt;
    List<ScoreDiffParam> scoreDiffs;

    //前段时间
    private String firstSecond;
    //前段占比
    private String firstSegmentWeight;

}
