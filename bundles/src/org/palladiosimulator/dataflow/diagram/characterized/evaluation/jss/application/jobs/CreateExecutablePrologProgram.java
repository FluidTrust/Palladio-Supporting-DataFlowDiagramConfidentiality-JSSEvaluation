package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard.MapValueLocation;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.VariableExtractor;

import de.uka.ipd.sdq.workflow.jobs.AbstractBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

public class CreateExecutablePrologProgram<T extends MapHoldingMDSDBlackboard>
        extends AbstractBlackboardInteractingJob<T> {

    private final MapValueLocation programLocation;
    private final MapValueLocation queryHelperLocation;
    private final MapValueLocation queryLocation;
    private final MapValueLocation executableProgramLocation;

    public CreateExecutablePrologProgram(MapValueLocation programLocation, MapValueLocation queryHelperLocation,
            MapValueLocation queryLocation, MapValueLocation executableProgramLocation) {
        this.programLocation = programLocation;
        this.queryHelperLocation = queryHelperLocation;
        this.queryLocation = queryLocation;
        this.executableProgramLocation = executableProgramLocation;
    }

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        List<String> fileContents = new ArrayList<>();
        fileContents.add(getStringFromBlackBoard(programLocation));
        fileContents.add(getStringFromBlackBoard(queryHelperLocation));

        String queryString = getStringFromBlackBoard(queryLocation);
        Collection<String> variableNames = VariableExtractor.determineVariables(queryString);
        String programGoal = ":-" + buildGoal(queryString, variableNames);
        fileContents.add(programGoal);

        String fileContent = fileContents.stream()
            .collect(Collectors.joining(System.lineSeparator()));
        getBlackboard().addValue(executableProgramLocation, fileContent);
    }

    protected String getStringFromBlackBoard(MapValueLocation location) {
        return (String) getBlackboard().getValue(location);
    }

    protected static String buildGoal(String goal, Collection<String> variables) {
        var goalParameter = goal.replaceAll("[\\r\\n]", "")
            .trim();
        if (goalParameter.endsWith(".")) {
            goalParameter = goalParameter.substring(0, goalParameter.length() - 1);
        }
        var writeGoals = variables.stream()
            .map(CreateExecutablePrologProgram::getVariablePrintGoals)
            .collect(Collectors.joining(", writeln(','), "));
        if (variables.isEmpty()) {
            writeGoals = "writeln(true)";
        }
        var actualGoal = "forall((Goal = (" + goalParameter + "), call(Goal)), (" + writeGoals
                + ", writeln(';'))), write(false).";
        return actualGoal;
    }

    protected static String getVariablePrintGoals(String variableName) {
        return String.format("write('%1$s = '), writeq(%1$s)", variableName);
    }

    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        // nothing to clean
    }

    @Override
    public String getName() {
        return "Create executable Prolog program";
    }

}
