package org.apache.fineract.portfolio.collateral.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.collateral.service.CollateralWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@CommandType(entity = "COLLATERALV2", action = "CREATE")
public class CreateCollateralCommandHandlerV2 implements NewCommandSourceHandler{
	private final CollateralWritePlatformService writePlatformService;

    @Autowired
    public CreateCollateralCommandHandlerV2(final CollateralWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.addCollateralV2(command);
    }
}
