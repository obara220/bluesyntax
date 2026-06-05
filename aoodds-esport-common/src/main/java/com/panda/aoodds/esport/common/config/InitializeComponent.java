package com.panda.aoodds.esport.common.config;

import cn.hutool.core.util.ObjectUtil;
import com.panda.aoodds.esport.common.constant.ConstantSystem;
import com.panda.aoodds.esport.common.entity.EuropeConvertMalay;
import com.panda.aoodds.esport.common.service.RedisService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 转换
 */
@Component
public class InitializeComponent implements CommandLineRunner {

    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate mongoTemplate;


    @Autowired
    private RedisService redisService;

    /**
     * 欧赔转马来赔缓存
     */
    @Getter
    private static Map<Double, Double> europeConvertMalayMap = new HashMap<>();

    /**
     * 马来转欧赔缓存
     */
    @Getter
    private static Map<Double, Double> malayConvertEuropeMap = new HashMap<>();


    @Override
    public void run(String... args) {
        //-----------初始化欧赔转马来的对应关系-----------
        europeConvertMalayMap = mongoTemplate.findAll(EuropeConvertMalay.class, ConstantSystem.EUROPE_CONVERT_MALAY).stream().collect(Collectors.toMap(EuropeConvertMalay::getEuropeValue, EuropeConvertMalay::getMalayValue));
        //-----------初始化马来转欧赔的对应关系-----------
        malayConvertEuropeMap = mongoTemplate.findAll(EuropeConvertMalay.class, ConstantSystem.MALAY_CONVERT_EUROPE).stream().collect(Collectors.toMap(EuropeConvertMalay::getMalayValue, EuropeConvertMalay::getEuropeValue));
        //全局开关
        Object aoMatchSwitchObj = redisService.get("AO_MATCH_SWITCH");
        if (ObjectUtil.isNull(aoMatchSwitchObj)) {
            redisService.setPermanent("AO_MATCH_SWITCH", Boolean.TRUE);
        }
    }

    /**
     * 马来转换欧赔
     *
     * @param malayOddsValue
     * @return
     */
    public Double getConvertMalayToEurope(Double malayOddsValue) {
        Double europeValue = malayConvertEuropeMap.get(malayOddsValue);
        if (null == europeValue) {
            europeValue = 0D;
        }
        return europeValue;
    }

}
