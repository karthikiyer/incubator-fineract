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
package org.apache.fineract.portfolio.collateral.service;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.collateral.api.CollateralApiConstants;
import org.apache.fineract.portfolio.collateral.api.CollateralApiConstants.COLLATERAL_JSON_INPUT_PARAMS;
import org.apache.fineract.portfolio.collateral.command.CollateralCommand;
import org.apache.fineract.portfolio.collateral.command.CollateralCommandV2;
import org.apache.fineract.portfolio.collateral.domain.Collateral;
import org.apache.fineract.portfolio.collateral.domain.CollateralBase;
import org.apache.fineract.portfolio.collateral.domain.CollateralBaseRepository;
import org.apache.fineract.portfolio.collateral.domain.CollateralRepositoryV2;
import org.apache.fineract.portfolio.collateral.domain.LoanCollateral;
import org.apache.fineract.portfolio.collateral.domain.LoanCollateralRepository;
import org.apache.fineract.portfolio.collateral.exception.CollateralCannotBeCreatedException;
import org.apache.fineract.portfolio.collateral.exception.CollateralCannotBeDeletedException;
import org.apache.fineract.portfolio.collateral.exception.CollateralCannotBeUpdatedException;
import org.apache.fineract.portfolio.collateral.exception.CollateralNotFoundException;
import org.apache.fineract.portfolio.collateral.exception.CollateralCannotBeCreatedException.LOAN_COLLATERAL_CANNOT_BE_CREATED_REASON;
import org.apache.fineract.portfolio.collateral.exception.CollateralCannotBeDeletedException.LOAN_COLLATERAL_CANNOT_BE_DELETED_REASON;
import org.apache.fineract.portfolio.collateral.exception.CollateralCannotBeUpdatedException.LOAN_COLLATERAL_CANNOT_BE_UPDATED_REASON;
import org.apache.fineract.portfolio.collateral.serialization.CollateralCommandFromApiJsonDeserializer;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepository;
import org.apache.fineract.portfolio.loanaccount.exception.LoanNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class CollateralWritePlatformServiceJpaRepositoryImpl implements CollateralWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(CollateralWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final LoanRepository loanRepository;
    private final LoanCollateralRepository collateralRepository;
    private final CollateralRepositoryV2 collateralRepositoryV2;
    private final CollateralBaseRepository collateralBaseRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final CollateralCommandFromApiJsonDeserializer collateralCommandFromApiJsonDeserializer;

    @Autowired
    public CollateralWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context, final LoanRepository loanRepository,
            final LoanCollateralRepository collateralRepository, final CodeValueRepositoryWrapper codeValueRepository,
            final CollateralCommandFromApiJsonDeserializer collateralCommandFromApiJsonDeserializer, final CollateralRepositoryV2 collateralRepositoryV2,
            final CollateralBaseRepository collateralBaseRepository) {
        this.context = context;
        this.loanRepository = loanRepository;
        this.collateralRepository = collateralRepository;
        this.codeValueRepository = codeValueRepository;
        this.collateralCommandFromApiJsonDeserializer = collateralCommandFromApiJsonDeserializer;
        this.collateralRepositoryV2 = collateralRepositoryV2;
        this.collateralBaseRepository=collateralBaseRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult addCollateral(final Long loanId, final JsonCommand command) {

        this.context.authenticatedUser();
        String jsonString=command.json();
        JsonObject json = (JsonObject)(new JsonParser().parse(jsonString));
        BigDecimal loanValue=new BigDecimal("0.0");
        String description="";
        Long type_cv_id=json.get("type_cv_id").getAsLong();
//        final CollateralCommand collateralCommand = this.collateralCommandFromApiJsonDeserializer.commandFromApiJson(command.json());
//        collateralCommand.validateForCreate();
        
        try {
            final Loan loan = this.loanRepository.findOne(loanId);
            if (loan == null) { throw new LoanNotFoundException(loanId); }
            
//            if (!loan.status().isSubmittedAndPendingApproval()) { throw new CollateralCannotBeCreatedException(
//                    LOAN_COLLATERAL_CANNOT_BE_CREATED_REASON.LOAN_NOT_IN_SUBMITTED_AND_PENDING_APPROVAL_STAGE, loan.getId()); }

            
            if(json.has("collateralDetails")){
            	JsonArray coll_details=json.get("collateralDetails").getAsJsonArray();
            	for(JsonElement j:coll_details){
            		JsonObject obj=(JsonObject)j;
            		Long units=obj.get("units").getAsLong();
            		Long collateralId=obj.get("collateralId").getAsLong();
            		Collateral c=collateralRepositoryV2.getOne(collateralId);
            		BigDecimal pctToBase=c.getPctToBase();
            		Long baseId=c.getBase().getId();
            		CollateralBase base=collateralBaseRepository.getOne(baseId);
            		BigDecimal baseValue=base.getBasePrice();
            		baseValue=baseValue.multiply(new BigDecimal(units.toString()));
            		baseValue=baseValue.multiply(pctToBase.divide(new BigDecimal("100.0")));
            		loanValue=loanValue.add(baseValue);
            		description=description+c.getId()+"C<->"+units+"U,";
            		
            	}
            }
            
            
    		
            final CodeValue collateralType = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                    CollateralApiConstants.COLLATERAL_CODE_NAME, type_cv_id);
            LoanCollateral loancollateral=new LoanCollateral(loan,collateralType,loanValue,description);
            this.collateralRepository.save(loancollateral);
//            final LoanCollateral collateral = LoanCollateral.fromJson(loan, collateralType, command);

            /**
             * Collaterals may be added only when the loan associated with them
             * are yet to be approved
             **/
            
//            this.collateralRepository.save(collateral);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withLoanId(loan.getId())//
                    .withEntityId(loancollateral.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleCollateralDataIntegrityViolation(dve);
            return CommandProcessingResult.empty();
        }
    }
    
    @Transactional
    @Override
    public CommandProcessingResult addCollateralV2(final JsonCommand command) {
    	
        this.context.authenticatedUser();
        String jsonString = command.json();
        JsonObject json = (JsonObject)(new JsonParser().parse(jsonString));
        Long base_id=json.get("base_id").getAsLong();
        String qualityStandard=json.get("qualityStandard").getAsString();
        BigDecimal pctToBase=json.get("percentageToBase").getAsBigDecimal();

        try {
           
            CollateralBase collateralBase=collateralBaseRepository.findOne(base_id);
            final Collateral collateral = new Collateral(collateralBase,qualityStandard,pctToBase);

            this.collateralRepositoryV2.save(collateral);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(collateral.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleCollateralDataIntegrityViolation(dve);
            return CommandProcessingResult.empty();
        }
    }
    
    @Transactional
    @Override
    public CommandProcessingResult addCollateralBase(final JsonCommand command) {
    	
    	this.context.authenticatedUser();
    	String jsonString = command.json();
    	JsonObject json = (JsonObject)(new JsonParser().parse(jsonString));
    	
    	Long cv_type_id=json.get("type_cv_id").getAsLong();
        BigDecimal basePrice=json.get("basePrice").getAsBigDecimal();

        try {
           
            final CodeValue collateralType = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                    CollateralApiConstants.COLLATERAL_CODE_NAME,cv_type_id );
            final CollateralBase collateralBase = new CollateralBase(collateralType, basePrice);

            this.collateralBaseRepository.save(collateralBase);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(collateralBase.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleCollateralDataIntegrityViolation(dve);
            return CommandProcessingResult.empty();
        }
    	
    }
    
    @Transactional
    @Override
    public CommandProcessingResult updateCollateral(final Long loanId, final Long collateralId, final JsonCommand command) {

        this.context.authenticatedUser();
        final CollateralCommand collateralCommand = this.collateralCommandFromApiJsonDeserializer.commandFromApiJson(command.json());
        collateralCommand.validateForUpdate();

        final Long collateralTypeId = collateralCommand.getCollateralTypeId();
        try {
            final Loan loan = this.loanRepository.findOne(loanId);
            if (loan == null) { throw new LoanNotFoundException(loanId); }

            CodeValue collateralType = null;

            final LoanCollateral collateralForUpdate = this.collateralRepository.findOne(collateralId);
            if (collateralForUpdate == null) { throw new CollateralNotFoundException(loanId, collateralId); }

            final Map<String, Object> changes = collateralForUpdate.update(command);

            if (changes.containsKey(COLLATERAL_JSON_INPUT_PARAMS.COLLATERAL_TYPE_ID.getValue())) {

                collateralType = this.codeValueRepository.findOneByCodeNameAndIdWithNotFoundDetection(
                        CollateralApiConstants.COLLATERAL_CODE_NAME, collateralTypeId);
                collateralForUpdate.setCollateralType(collateralType);
            }

            /**
             * Collaterals may be updated only when the loan associated with
             * them are yet to be approved
             **/
            if (!loan.status().isSubmittedAndPendingApproval()) { throw new CollateralCannotBeUpdatedException(
                    LOAN_COLLATERAL_CANNOT_BE_UPDATED_REASON.LOAN_NOT_IN_SUBMITTED_AND_PENDING_APPROVAL_STAGE, loan.getId()); }

            if (!changes.isEmpty()) {
                this.collateralRepository.saveAndFlush(collateralForUpdate);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withLoanId(command.getLoanId())//
                    .withEntityId(collateralId) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleCollateralDataIntegrityViolation(dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }
    
    @Transactional
    @Override
    public CommandProcessingResult updateCollateralV2(final Long collateralId, final JsonCommand command) {

        this.context.authenticatedUser();
        String jsonString=command.json();
        JsonObject json=(JsonObject)(new JsonParser().parse(jsonString));

        
        try {
          
            Collateral collateral=collateralRepositoryV2.findOne(collateralId);
            
            if(!jsonString.equals("{}")){
            	
            	if(json.has("pctToBase")){
            		collateral.setPctToBase(json.get("pctToBase").getAsBigDecimal());
            	}
            	
            	if(json.has("quality_standard")){
            		collateral.setQuality(json.get("quality_standard").getAsString());
            	}
            	
            	this.collateralRepositoryV2.saveAndFlush(collateral);
            	
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(collateralId) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleCollateralDataIntegrityViolation(dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteCollateral(final Long loanId, final Long collateralId, final Long commandId) {
        final Loan loan = this.loanRepository.findOne(loanId);
        if (loan == null) { throw new LoanNotFoundException(loanId); }
        final LoanCollateral collateral = this.collateralRepository.findByLoanIdAndId(loanId, collateralId);
        if (collateral == null) { throw new CollateralNotFoundException(loanId, collateralId); }

        /**
         * Collaterals may be deleted only when the loan associated with them
         * are yet to be approved
         **/
        if (!loan.status().isSubmittedAndPendingApproval()) { throw new CollateralCannotBeDeletedException(
                LOAN_COLLATERAL_CANNOT_BE_DELETED_REASON.LOAN_NOT_IN_SUBMITTED_AND_PENDING_APPROVAL_STAGE, loanId, collateralId); }

        loan.getCollateral().remove(collateral);
        this.collateralRepository.delete(collateral);

        return new CommandProcessingResultBuilder().withCommandId(commandId).withLoanId(loanId).withEntityId(collateralId).build();
    }

    private void handleCollateralDataIntegrityViolation(final DataIntegrityViolationException dve) {
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.collateral.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }

}