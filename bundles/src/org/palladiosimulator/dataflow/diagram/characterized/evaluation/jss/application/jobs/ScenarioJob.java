package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.dataflow.confidentiality.transformation.workflow.jobs.LoadModelJob;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.Activator;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard.MapValueLocation;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.Case;
import org.palladiosimulator.supporting.prolog.api.PrologAPI;
import org.prolog4j.IProverFactory;

import de.uka.ipd.sdq.workflow.jobs.IJob;
import de.uka.ipd.sdq.workflow.jobs.SequentialBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.ModelLocation;

public abstract class ScenarioJob<T extends MapHoldingMDSDBlackboard> extends SequentialBlackboardInteractingJob<T> {

    protected static final String PROLOG_PROGRAM_KEY = "prolog-program";
    protected static final String PROLOG_QUERYHELPER_KEY = "prolog-query-helper";
    protected static final String PROLOG_QUERY_KEY = "prolog-query";
    protected static final String PROLOG_SOLUTION_KEY = "prolog-solution";
    protected static final String PROLOG_EXECUTABLEPROG_KEY = "prolog-program-executable";
    protected static final String RESULT_METRICS_KEY = "result-metrics";

    protected ScenarioJob(String partitionId, Case caseToExecute, Function<Case, File> dfdGetter,
            File destinationFolder) {
        addAll(createJobs(partitionId, caseToExecute, dfdGetter, destinationFolder));
    }

    protected static List<IJob> createJobs(String partitionId, Case caseToExecute, Function<Case, File> dfdGetter,
            File destinationFolder) {
        List<IJob> jobs = new ArrayList<>();
        jobs.add(createLoadDFDJob(partitionId, dfdGetter.apply(caseToExecute)));
        jobs.add(createMetricCollectionJob(caseToExecute.getCaseName(), partitionId, dfdGetter.apply(caseToExecute),
                RESULT_METRICS_KEY));
        jobs.add(createTransformDFDJob(partitionId, dfdGetter.apply(caseToExecute), PROLOG_PROGRAM_KEY));
        jobs.add(createLoadPrologQueryJob(partitionId, PROLOG_QUERYHELPER_KEY, PROLOG_QUERY_KEY,
                caseToExecute.getQueryHelperFile()
                    .orElse(null),
                caseToExecute.getQueryFile()));
        jobs.add(createRunPrologAnalysisJob(createProverFactory(), partitionId, PROLOG_PROGRAM_KEY,
                PROLOG_QUERYHELPER_KEY, PROLOG_QUERY_KEY, PROLOG_SOLUTION_KEY));
        jobs.add(createCreateExecutablePrologProgram(partitionId, PROLOG_PROGRAM_KEY, PROLOG_QUERYHELPER_KEY,
                PROLOG_QUERY_KEY, PROLOG_EXECUTABLEPROG_KEY));
        jobs.add(createSerializeResultsJob(partitionId, dfdGetter.apply(caseToExecute), PROLOG_QUERYHELPER_KEY,
                PROLOG_QUERY_KEY, PROLOG_EXECUTABLEPROG_KEY, PROLOG_SOLUTION_KEY, RESULT_METRICS_KEY,
                destinationFolder));
        return jobs;
    }

    protected static IProverFactory createProverFactory() {
        return Activator.getInstance()
            .getProverManager()
            .getProvers()
            .values()
            .iterator()
            .next();
    }

    protected static PrologAPI createPrologAPI() {
        return Activator.getInstance()
            .getPrologAPI();
    }

    protected static LoadModelJob<? extends MapHoldingMDSDBlackboard> createLoadDFDJob(String partitionId,
            File dfdModel) {
        ModelLocation dfdLocation = new ModelLocation(partitionId, URI.createFileURI(dfdModel.getAbsolutePath()));
        return new LoadModelJob<>(dfdLocation);
    }

    protected static TransformDFDToPrologJob<? extends MapHoldingMDSDBlackboard> createTransformDFDJob(
            String partitionId, File dfdModel, String prologProgramKey) {
        ModelLocation dfdLocation = new ModelLocation(partitionId, URI.createFileURI(dfdModel.getAbsolutePath()));
        MapValueLocation prologLocation = new MapValueLocation(partitionId, prologProgramKey);
        return new TransformDFDToPrologJob<>(dfdLocation, prologLocation);
    }

    protected static LoadPrologQueryJob<? extends MapHoldingMDSDBlackboard> createLoadPrologQueryJob(String partitionId,
            String queryHelperKey, String queryKey, File queryHelperFile, File queryFile) {
        MapValueLocation queryLocation = new MapValueLocation(partitionId, queryKey);
        MapValueLocation queryHelperLocation = new MapValueLocation(partitionId, queryHelperKey);
        return new LoadPrologQueryJob<>(queryHelperFile, queryFile, queryHelperLocation, queryLocation);
    }

    protected static RunPrologAnalysis<? extends MapHoldingMDSDBlackboard> createRunPrologAnalysisJob(
            IProverFactory proverFactory, String partitionId, String programKey, String queryHelperKey, String queryKey,
            String solutionKey) {
        MapValueLocation programLocation = new MapValueLocation(partitionId, programKey);
        MapValueLocation queryHelperLocation = new MapValueLocation(partitionId, queryHelperKey);
        MapValueLocation queryLocation = new MapValueLocation(partitionId, queryKey);
        MapValueLocation solutionLocation = new MapValueLocation(partitionId, solutionKey);
        return new RunPrologAnalysis<>(proverFactory, programLocation, queryHelperLocation, queryLocation,
                solutionLocation);
    }

    protected static CreateExecutablePrologProgram<? extends MapHoldingMDSDBlackboard> createCreateExecutablePrologProgram(
            String partitionId, String programKey, String queryHelperKey, String queryKey, String executableKey) {
        MapValueLocation programLocation = new MapValueLocation(partitionId, programKey);
        MapValueLocation queryHelperLocation = new MapValueLocation(partitionId, queryHelperKey);
        MapValueLocation queryLocation = new MapValueLocation(partitionId, queryKey);
        MapValueLocation executableProgramLocation = new MapValueLocation(partitionId, executableKey);
        return new CreateExecutablePrologProgram<>(programLocation, queryHelperLocation, queryLocation,
                executableProgramLocation);
    }

    protected static MetricCollectionJob<? extends MapHoldingMDSDBlackboard> createMetricCollectionJob(String caseName,
            String partitionId, File dfdModel, String metricsKey) {
        ModelLocation dfdLocation = new ModelLocation(partitionId, URI.createFileURI(dfdModel.getAbsolutePath()));
        MapValueLocation metricsLocation = new MapValueLocation(partitionId, metricsKey);
        return new MetricCollectionJob<>(caseName, dfdLocation, metricsLocation);
    }

    protected static SerializeResultsJob<? extends MapHoldingMDSDBlackboard> createSerializeResultsJob(
            String partitionId, File dfdModel, String queryHelperKey, String queryKey, String executableKey,
            String solutionKey, String resultMetricsKey, File destinationDir) {
        ModelLocation dfdLocation = new ModelLocation(partitionId, URI.createFileURI(dfdModel.getAbsolutePath()));
        MapValueLocation queryHelperLocation = new MapValueLocation(partitionId, queryHelperKey);
        MapValueLocation queryLocation = new MapValueLocation(partitionId, queryKey);
        MapValueLocation executableProgramLocation = new MapValueLocation(partitionId, executableKey);
        MapValueLocation solutionLocation = new MapValueLocation(partitionId, solutionKey);
        MapValueLocation resultMetricsLocation = new MapValueLocation(partitionId, resultMetricsKey);
        PrologAPI prologAPI = createPrologAPI();
        return new SerializeResultsJob<>(prologAPI, createProverFactory(), dfdLocation, queryHelperLocation,
                queryLocation, executableProgramLocation, solutionLocation, resultMetricsLocation, destinationDir);
    }
}
