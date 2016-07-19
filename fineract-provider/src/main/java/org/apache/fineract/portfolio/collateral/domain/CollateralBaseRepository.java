package org.apache.fineract.portfolio.collateral.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CollateralBaseRepository extends JpaRepository<CollateralBase, Long>, JpaSpecificationExecutor<CollateralBase> {

}
