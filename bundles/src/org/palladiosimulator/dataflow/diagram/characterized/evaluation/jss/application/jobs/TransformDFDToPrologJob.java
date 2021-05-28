package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.palladiosimulator.dataflow.confidentiality.transformation.workflow.TransformDFDToPrologWorkflow;
import org.palladiosimulator.dataflow.confidentiality.transformation.workflow.TransformationWorkflowBuilder;
import org.palladiosimulator.dataflow.diagram.DataFlowDiagram.DataFlowDiagram;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard.MapValueLocation;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.DataDictionaryCharacterized;

import de.uka.ipd.sdq.workflow.jobs.AbstractBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.ModelLocation;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;

public class TransformDFDToPrologJob<T extends MapHoldingMDSDBlackboard> extends AbstractBlackboardInteractingJob<T> {

    private final ModelLocation dfdModelLocation;
    private final MapValueLocation prologProgramLocation;

    public TransformDFDToPrologJob(ModelLocation dfdModelLocation, MapValueLocation prologProgramLocation) {
        this.dfdModelLocation = dfdModelLocation;
        this.prologProgramLocation = prologProgramLocation;
    }

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        ResourceSetPartition dfdPartition = getBlackboard().getPartition(dfdModelLocation.getPartitionID());
        dfdPartition.resolveAllProxies();
        DataFlowDiagram dfd = (DataFlowDiagram) getBlackboard().getContents(dfdModelLocation)
            .get(0);
        DataDictionaryCharacterized dd = dfdPartition.getResourceSet()
            .getResources()
            .stream()
            .map(Resource::getContents)
            .flatMap(Collection::stream)
            .filter(DataDictionaryCharacterized.class::isInstance)
            .map(DataDictionaryCharacterized.class::cast)
            .findFirst()
            .get();
        
        Copier copier = new EcoreUtil.Copier();
        copier.copyAll(Arrays.asList(dd, dfd));
        copier.copyReferences();
        DataDictionaryCharacterized ddCopy = (DataDictionaryCharacterized) copier.get(dd);
        DataFlowDiagram dfdCopy = (DataFlowDiagram) copier.get(dfd);

        TransformationWorkflowBuilder builder = new TransformationWorkflowBuilder();
        TransformDFDToPrologWorkflow transformationWorkflow = builder.addDFD(dfdCopy, ddCopy)
            .addSerializeToString()
            .build();

        transformationWorkflow.run();
        String prologProgram = transformationWorkflow.getSerializedPrologProgram()
            .get();

        getBlackboard().addValue(prologProgramLocation, prologProgram);
    }

    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        // nothing to cleanup
    }

    @Override
    public String getName() {
        return "Transform DFD to Prolog Program";
    }

}
