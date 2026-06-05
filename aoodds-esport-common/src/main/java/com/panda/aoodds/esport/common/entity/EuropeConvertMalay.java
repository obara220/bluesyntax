package com.panda.aoodds.esport.common.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 欧赔 - 马来
 * 马来 - 欧赔
 */
@Data
public class EuropeConvertMalay {
    @Field("europe_value")
    Double europeValue;
    @Field("malay_value")
    Double malayValue;
}
