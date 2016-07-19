package org.apache.fineract.portfolio.collateral.domain;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.collateral.api.CollateralApiConstants.COLLATERAL_JSON_INPUT_PARAMS;
import org.apache.fineract.portfolio.collateral.data.CollateralData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_collateral")
public class Collateral extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "base_id", nullable = false)
    private CollateralBase base;

    @Column(name = "quality_standard" )
    private String quality;

    @Column(name = "pct_to_base", scale=2, precision=5)
    private BigDecimal pctToBase ;

    public Collateral (final CollateralBase base, final String quality, final BigDecimal pctToBase) {
        this.base=base;
        this.quality=quality;
        this.pctToBase=pctToBase;
    }
    
    protected Collateral() {
        //
    }
    
    public void setQuality(String quality){
    	this.quality=quality;
    }
    
    public void setPctToBase(BigDecimal pctToBase){
    	this.pctToBase=pctToBase;
    }
    
    public CollateralBase getBase(){
    	return this.base;
    }
    
    public String getQuality(){
    	return this.quality;
    }
    
    public BigDecimal getPctToBase(){
    	return this.pctToBase;
    }
    
    
}