package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.io.File;

import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.Case;

import de.uka.ipd.sdq.workflow.BlackboardBasedWorkflow;
import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.jobs.SequentialBlackboardInteractingJob;

public class CaseJob extends BlackboardBasedWorkflow<MapHoldingMDSDBlackboard> {

    public CaseJob(Case caseToExecute, File destinationFolder) {
        super(createCaseJob(caseToExecute, destinationFolder), new MapHoldingMDSDBlackboard());
    }

    protected static IJob createCaseJob(Case caseToExecute, File destinationFolder) {
        SequentialBlackboardInteractingJob<MapHoldingMDSDBlackboard> job = new SequentialBlackboardInteractingJob<>(
                "Scenario Execution Job");

        File caseFolder = new File(destinationFolder, caseToExecute.getCaseName());

        File noViolationFolder = new File(caseFolder, "no_violation");
        job.add(new ScenarioWithoutViolationJob<MapHoldingMDSDBlackboard>(caseToExecute, noViolationFolder));

        File violationFolder = new File(caseFolder, "violation");
        job.add(new ScenarioWithViolationJob<MapHoldingMDSDBlackboard>(caseToExecute, violationFolder));

        job.add(new CopyFilesJob(caseFolder, caseToExecute.getAuxiliarFiles()));
        
        return job;
    }

}
