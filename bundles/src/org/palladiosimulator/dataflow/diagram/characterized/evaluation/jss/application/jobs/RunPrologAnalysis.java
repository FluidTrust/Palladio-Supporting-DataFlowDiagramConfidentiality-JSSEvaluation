package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard.MapValueLocation;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.VariableExtractor;
import org.prolog4j.IProverFactory;
import org.prolog4j.Prover;
import org.prolog4j.Query;
import org.prolog4j.Solution;
import org.prolog4j.SolutionIterator;
import org.prolog4j.UnknownVariableException;

import de.uka.ipd.sdq.workflow.jobs.AbstractBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

public class RunPrologAnalysis<T extends MapHoldingMDSDBlackboard> extends AbstractBlackboardInteractingJob<T> {

    private final IProverFactory proverFactory;
    private final MapValueLocation programLocation;
    private final MapValueLocation queryHelperLocation;
    private final MapValueLocation queryLocation;
    private final MapValueLocation solutionLocation;

    public RunPrologAnalysis(IProverFactory proverFactory, MapValueLocation programLocation,
            MapValueLocation queryHelperLocation, MapValueLocation queryLocation, MapValueLocation solutionLocation) {
        this.proverFactory = proverFactory;
        this.programLocation = programLocation;
        this.queryHelperLocation = queryHelperLocation;
        this.queryLocation = queryLocation;
        this.solutionLocation = solutionLocation;
    }

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        Prover prover = proverFactory.createProver();

        prover.addTheory((String) getBlackboard().getValue(programLocation));
        prover.addTheory((String) getBlackboard().getValue(queryHelperLocation));

        String queryString = (String) getBlackboard().getValue(queryLocation);
        Query query = prover.query(queryString);
        Solution<Object> solution = query.solve();
        List<SortedMap<String, Object>> solutions = writeSolutionsIntoBlackboard(queryString, solution);

        getBlackboard().addValue(solutionLocation, solutions);
    }

    protected static List<SortedMap<String, Object>> writeSolutionsIntoBlackboard(String goal, Solution<Object> solution) {
        Collection<String> variableNames = VariableExtractor.determineVariables(goal);

        // collect results
        List<SortedMap<String, Object>> solutions = new ArrayList<>();
        for (SolutionIterator<Object> solutionIter = solution.iterator(); solutionIter.hasNext(); solutionIter.next()) {
            SortedMap<String, Object> solutionVariables = new TreeMap<>();
            for (String variableName : variableNames) {
                try {
                    solutionVariables.put(variableName, solutionIter.get(variableName));
                } catch (UnknownVariableException e) {
                    // skip variable (this does not imply an error)
                }
            }
            solutions.add(solutionVariables);
        }

        return solutions;
    }

    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        // nothing to cleanup
    }

    @Override
    public String getName() {
        return "Run Prolog Analysis";
    }

}
