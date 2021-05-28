package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.io.File;
import java.nio.file.Files;

import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard.MapValueLocation;

import de.uka.ipd.sdq.workflow.jobs.AbstractBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

public class LoadPrologQueryJob<T extends MapHoldingMDSDBlackboard> extends AbstractBlackboardInteractingJob<T> {

    private final File queryHelperFile;
    private final File queryFile;
    private final MapValueLocation queryHelperLocation;
    private final MapValueLocation queryLocation;

    public LoadPrologQueryJob(File queryHelperFile, File queryFile, MapValueLocation queryHelperLocation, MapValueLocation queryLocation) {
        this.queryHelperFile = queryHelperFile;
        this.queryFile = queryFile;
        this.queryHelperLocation = queryHelperLocation;
        this.queryLocation = queryLocation;
    }
    
    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        getBlackboard().addValue(queryHelperLocation, readFileOrDefault(queryHelperFile, ""));
        getBlackboard().addValue(queryLocation, readFileOrDefault(queryFile, ""));
    }

    protected String readFileOrDefault(File file, String defaultValue) {
        try {
            return Files.readString(file.toPath());
        } catch (Throwable e) {
            return defaultValue;
        }
    }
    
    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        // nothing to clean up
    }

    @Override
    public String getName() {
        return "Load Prolog Query";
    }

}
