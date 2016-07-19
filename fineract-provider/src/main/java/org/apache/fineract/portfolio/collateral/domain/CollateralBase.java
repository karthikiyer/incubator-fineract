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
@Table(name = "m_collateral_base_value")
public class CollateralBase extends AbstractPersistable<Long> {

    @ManyToOne
    @JoinColumn(name = "type_cv_id", nullable = false)
    private CodeValue type;

    @Column(name = "base_price", scale=2, precision=10)
    private BigDecimal basePrice ;

    public CollateralBase (final CodeValue collateralType, final BigDecimal basePrice) {
        this.type=collateralType;
        this.basePrice=basePrice;
    }
    
    public void setPctToBase(BigDecimal pctToBase){
    	this.basePrice=pctToBase;
    }
    
    public BigDecimal getBasePrice(){
    	return this.basePrice;
    }
    
    protected CollateralBase() {
        //
    }
    
}