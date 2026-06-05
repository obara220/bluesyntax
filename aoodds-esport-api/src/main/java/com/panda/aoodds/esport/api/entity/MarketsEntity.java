package com.panda.aoodds.esport.api.entity;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Author carson
 * @DATE 2022/1/16 16:42
 **/
@Data
@AllArgsConstructor
public class MarketsEntity implements java.io.Serializable {

    Integer marketId;
    String marketName;
    String handicap;
    Integer status;
    Integer order;
    Double realTimeMargin;
    Long modifyTime;
    List<BetPieceEntity> betPieceEntities;

    public MarketsEntity() {
    }

    public MarketsEntity newExample() {
        return new MarketsEntity(this.getMarketId(), this.getMarketName(), this.getHandicap(), this.getStatus(),
                this.getOrder(), this.getRealTimeMargin(), this.getModifyTime(), this.getBetPieceEntities());
    }
}
