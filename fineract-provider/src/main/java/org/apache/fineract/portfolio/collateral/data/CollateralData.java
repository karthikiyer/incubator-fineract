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
package org.apache.fineract.portfolio.collateral.data;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.collateral.domain.Collateral;
import org.apache.fineract.portfolio.collateral.domain.CollateralRepositoryV2;

/**
 * Immutable data object for Collateral data.
 */
public class CollateralData {

    private final Long id;
    private final CollateralProductData collateralDetails;
    private final BigDecimal value;
    private final BigDecimal quantity;
    @SuppressWarnings("unused")
    private final Collection<CodeValueData> allowedCollateralTypes;
    private final CurrencyData currency;

    public static CollateralData instance(final Long id, final CollateralProductData collateral, final BigDecimal value, final BigDecimal quantity,
            final CurrencyData currencyData) {
        return new CollateralData(id, collateral, value, quantity, currencyData);
    }

    public static CollateralData template(final Collection<CodeValueData> codeValues) {
        return new CollateralData(null, null, null, null, null, codeValues);
    }

    private CollateralData(final Long id, final CollateralProductData collateral, final BigDecimal value, final BigDecimal quantity,
            final CurrencyData currencyData) {
        this.id = id;
        this.collateralDetails = collateral;
        this.value = value;
        this.quantity = quantity;
        this.currency = currencyData;
        this.allowedCollateralTypes = null;
    }

    private CollateralData(final Long id, final CollateralProductData collateral, final BigDecimal value, final BigDecimal quantity,
            final CurrencyData currencyData, final Collection<CodeValueData> allowedCollateralTypes) {
        this.id = id;
        this.collateralDetails = collateral;
        this.value = value;
        this.quantity = quantity;
        this.currency = currencyData;
        this.allowedCollateralTypes = allowedCollateralTypes;
    }

    public CollateralData template(final CollateralData collateralData, final Collection<CodeValueData> codeValues) {
        return new CollateralData(collateralData.id, collateralData.collateralDetails, collateralData.value, collateralData.quantity,
                collateralData.currency, codeValues);
    }
    
}