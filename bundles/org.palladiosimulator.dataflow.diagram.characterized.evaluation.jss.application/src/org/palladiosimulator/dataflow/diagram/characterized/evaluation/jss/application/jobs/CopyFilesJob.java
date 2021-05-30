package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import de.uka.ipd.sdq.workflow.jobs.AbstractJob;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

public class CopyFilesJob extends AbstractJob {

    private final File destinationFolder;
    private final Collection<File> filesToCopy;

    public CopyFilesJob(File destinationFolder, Collection<File> filesToCopy) {
        this.destinationFolder = destinationFolder;
        this.filesToCopy = filesToCopy;
    }
    
    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        for (File f : filesToCopy) {
            try {
                Files.copy(f.toPath(), new File(destinationFolder, f.getName()).toPath());
            } catch (IOException e) {
                throw new JobFailedException("Could not copy file.", e);
            }
        }
    }

    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        // nothing to clean up
    }

    @Override
    public String getName() {
        return "Serialize Auxiliar Files";
    }

}
