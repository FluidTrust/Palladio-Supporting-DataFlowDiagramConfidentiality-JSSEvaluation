package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.io.File;

import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.Case;

public class ScenarioWithViolationJob<T extends MapHoldingMDSDBlackboard> extends ScenarioJob<T> {

    private static final String VIOLATION_PARTITION_ID = "violation";

    public ScenarioWithViolationJob(Case caseToExecute, File destinationFolder) {
        super(VIOLATION_PARTITION_ID, caseToExecute, Case::getDfdViolationModel, destinationFolder);
    }
}
