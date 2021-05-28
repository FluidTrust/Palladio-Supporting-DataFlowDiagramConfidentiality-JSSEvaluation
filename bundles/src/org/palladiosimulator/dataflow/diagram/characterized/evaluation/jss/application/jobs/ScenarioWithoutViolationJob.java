package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.io.File;

import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.Case;

public class ScenarioWithoutViolationJob<T extends MapHoldingMDSDBlackboard> extends ScenarioJob<T> {

    private static final String REGULAR_PARTITION_ID = "noviolation";

    public ScenarioWithoutViolationJob(Case caseToExecute, File destinationFolder) {
        super(REGULAR_PARTITION_ID, caseToExecute, Case::getDfdModel, destinationFolder);
    }

}
