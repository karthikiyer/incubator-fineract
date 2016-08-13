/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.collateral.domain;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.CascadeType;
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
import org.hibernate.annotations.Cascade;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_loan_collateral")
public class LoanCollateral extends AbstractPersistable<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "collateral_id")
    private Collateral collateral;

    @Column(name = "value", scale = 6, precision = 19)
    private BigDecimal value;

    @Column(name = "quantity", length = 500)
    private BigDecimal quantity;

    public static LoanCollateral from(final Collateral collateral, final BigDecimal value, final BigDecimal quantity) {
        return new LoanCollateral(null, collateral, value, quantity);
    }

    protected LoanCollateral() {
        //
    }

    public LoanCollateral(final Loan loan, final Collateral collateral, final BigDecimal value, final BigDecimal quantity) {
        this.loan = loan;
        this.collateral = collateral;
        this.value = value;
        this.quantity = quantity;
    }

    public void assembleFrom(final Collateral collateral, final BigDecimal value, final BigDecimal quantity) {
        this.collateral = collateral;
        this.quantity = quantity;
        this.value = value;
    }

    public void associateWith(final Loan loan) {
        this.loan = loan;
    }

    public static LoanCollateral fromJson(final Loan loan, final Collateral collateral, final JsonCommand command) {
        final BigDecimal quantity = command.bigDecimalValueOfParameterNamed(COLLATERAL_JSON_INPUT_PARAMS.QUANTITY.getValue());
        final BigDecimal value = command.bigDecimalValueOfParameterNamed(COLLATERAL_JSON_INPUT_PARAMS.VALUE.getValue());
        return new LoanCollateral(loan, collateral, value, quantity);
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String collateralIdParamName = COLLATERAL_JSON_INPUT_PARAMS.COLLATERAL_TYPE_ID.getValue();
        if (command.isChangeInLongParameterNamed(collateralIdParamName, this.collateral.getId())) {
            final Long newValue = command.longValueOfParameterNamed(collateralIdParamName);
            actualChanges.put(collateralIdParamName, newValue);
        }

        final String quantityParamName = COLLATERAL_JSON_INPUT_PARAMS.QUANTITY.getValue();
        if (command.isChangeInBigDecimalParameterNamed(quantityParamName, this.quantity)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(quantityParamName);
            actualChanges.put(quantityParamName, newValue);
            this.quantity = newValue ;
        }

        final String valueParamName = COLLATERAL_JSON_INPUT_PARAMS.VALUE.getValue();
        if (command.isChangeInBigDecimalParameterNamed(valueParamName, this.value)) {
            final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(valueParamName);
            actualChanges.put(valueParamName, newValue);
            this.value = newValue;
        }

        return actualChanges;
    }

//    public CollateralData toData() {
//        final Collateral collateral = this.type.toData();
//        return CollateralData.instance(getId(), typeData, this.value, this.description, null);
//    }

    public void setCollateral(final Collateral collateral) {
        this.collateral = collateral;
    }

//    @Override
//    public boolean equals(final Object obj) {
//        if (obj == null) { return false; }
//        if (obj == this) { return true; }
//        if (obj.getClass() != getClass()) { return false; }
//        final LoanCollateral rhs = (LoanCollateral) obj;
//        return new EqualsBuilder().appendSuper(super.equals(obj)) //
//                .append(getId(), rhs.getId()) //
//                .append(this.type.getId(), rhs.type.getId()) //
//                .append(this.description, rhs.description) //
//                .append(this.value, this.value)//
//                .isEquals();
//    }
//
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder(3, 5) //
//                .append(getId()) //
//                .append(this.type.getId()) //
//                .append(this.description) //
//                .append(this.value)//
//                .toHashCode();
//    }
}