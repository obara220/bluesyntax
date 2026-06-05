package com.panda.aoodds.esport.common.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


/**
 * @Author carson
 * @DATE 2022/3/10 13:28
 **/
@Slf4j
@Data
public class AoMatchInfoEntity implements Serializable,Cloneable{
    String id;
    Long aoMatchId;
    Long beginTime;
    Integer sportId;
    Long standardMatchId;
    Integer tournamentLevel;
    Long standardTouId;
    Long standardHomeId;
    Long standardAwayId;
    Long createTime;
    Integer matchLength;
    @Override
    public Object clone() {
        AoMatchInfoEntity scoreParam = null;
        try {
            //克隆后需要转型
            scoreParam = (AoMatchInfoEntity) super.clone();
            //此处需要捕捉异常
        } catch (Exception e) {
            log.error("克隆对象出错，异常信息：", e);
        }
        return scoreParam;
    }
}
