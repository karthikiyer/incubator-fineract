package org.apache.fineract.portfolio.collateral.command;

import java.math.BigDecimal;

public class CollateralCommandV2 {
	private final Long collateralTypeId;
    private final String qualityStandard;
    private final BigDecimal percentageToBase;

    public CollateralCommandV2(final Long collateralTypeId, final String qualityStandard, final BigDecimal percentageToBase) {
        this.collateralTypeId = collateralTypeId;
        this.qualityStandard = qualityStandard;
        this.percentageToBase = percentageToBase;
    }

    public Long getCollateralTypeId() {
        return this.collateralTypeId;
    }

    public String getQualityStandard() {
        return this.qualityStandard;
    }

    public BigDecimal getPercentageToBase() {
        return this.percentageToBase;
    }

}
