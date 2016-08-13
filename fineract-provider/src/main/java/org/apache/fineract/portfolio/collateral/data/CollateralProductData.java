package org.apache.fineract.portfolio.collateral.data;

import java.math.BigDecimal;

public class CollateralProductData {

	private final String codeValueName;
	private final String quality;
	private final BigDecimal basePrice;
	private final BigDecimal pctToBase;
	
	public static CollateralProductData instance(final String codeValueName, final String quality, final BigDecimal basePrice,
			final BigDecimal pctToBase){
		return new CollateralProductData(codeValueName, quality, basePrice, pctToBase);
	}
	
	private CollateralProductData(final String codeValueName, final String quality, final BigDecimal basePrice,
			final BigDecimal pctToBase){
		this.codeValueName=codeValueName;
		this.quality=quality;
		this.basePrice=basePrice;
		this.pctToBase=pctToBase;
		
	}
}
