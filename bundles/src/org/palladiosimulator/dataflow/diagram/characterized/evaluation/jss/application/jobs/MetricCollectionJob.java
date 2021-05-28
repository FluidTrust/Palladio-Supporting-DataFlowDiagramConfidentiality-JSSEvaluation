package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.dataflow.diagram.DataFlowDiagram.DataFlowDiagram;
import org.palladiosimulator.dataflow.diagram.DataFlowDiagram.ExternalActor;
import org.palladiosimulator.dataflow.diagram.DataFlowDiagram.Store;
import org.palladiosimulator.dataflow.diagram.characterized.DataFlowDiagramCharacterized.CharacterizedActorProcess;
import org.palladiosimulator.dataflow.diagram.characterized.DataFlowDiagramCharacterized.CharacterizedNode;
import org.palladiosimulator.dataflow.diagram.characterized.DataFlowDiagramCharacterized.CharacterizedProcess;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard.MapValueLocation;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.Metrics;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.BehaviorDefinition;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.Characteristic;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.DataDictionaryCharacterized;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.Enumeration;

import de.uka.ipd.sdq.workflow.jobs.AbstractBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.ModelLocation;

public class MetricCollectionJob<T extends MapHoldingMDSDBlackboard> extends AbstractBlackboardInteractingJob<T> {

    private final String caseName;
    private final ModelLocation dfdLocation;
    private final MapValueLocation metricsLocation;

    public MetricCollectionJob(String caseName, ModelLocation dfdLocation, MapValueLocation metricsLocation) {
        this.caseName = caseName;
        this.dfdLocation = dfdLocation;
        this.metricsLocation = metricsLocation;
    }

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        DataFlowDiagram dfd = (DataFlowDiagram) getBlackboard().getContents(dfdLocation)
            .get(0);
        Metrics metrics = processDfd(dfd);
        getBlackboard().addValue(metricsLocation, metrics);
    }

    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        // nothing to cleanup
    }

    @Override
    public String getName() {
        return "Metric Collection Task";
    }

    protected Metrics processDfd(DataFlowDiagram dfd) {
        EcoreUtil.resolveAll(dfd.eResource());
        Set<DataDictionaryCharacterized> ddcs = dfd.eResource()
            .getResourceSet()
            .getResources()
            .stream()
            .map(Resource::getContents)
            .flatMap(Collection::stream)
            .filter(DataDictionaryCharacterized.class::isInstance)
            .map(DataDictionaryCharacterized.class::cast)
            .collect(Collectors.toSet());
        if (ddcs.size() != 1) {
            throw new IllegalStateException("more than one DDC");
        }
        DataDictionaryCharacterized ddc = ddcs.iterator()
            .next();

        List<CharacterizedNode> characterizedNodes = dfd.getNodes()
            .stream()
            .filter(CharacterizedNode.class::isInstance)
            .map(CharacterizedNode.class::cast)
            .collect(Collectors.toList());
        Set<BehaviorDefinition> nodeBehaviors = characterizedNodes.stream()
            .map(CharacterizedNode::getBehavior)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Set<EList<Characteristic>> nodeCharacteristics = characterizedNodes.stream()
            .map(CharacterizedNode::getCharacteristics)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Metrics metrics = new Metrics();
        metrics.filename = caseName;

        metrics.edges = dfd.getEdges()
            .size();
        metrics.nodes = dfd.getNodes()
            .size();
        metrics.behaviors = nodeBehaviors.size();
        metrics.characteristicTypes = ddc.getCharacteristicTypes()
            .size();
        metrics.nodeCharacteristics = nodeCharacteristics.size();
        metrics.labels = ddc.getEnumerations()
            .stream()
            .map(Enumeration::getLiterals)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet())
            .size();
        metrics.enumerations = ddc.getEnumerations()
            .size();
        metrics.stores = dfd.getNodes()
            .stream()
            .filter(Store.class::isInstance)
            .count();
        metrics.actors = dfd.getNodes()
            .stream()
            .filter(ExternalActor.class::isInstance)
            .count();
        metrics.processes = dfd.getNodes()
            .stream()
            .filter(n -> n instanceof CharacterizedProcess && !(n instanceof CharacterizedActorProcess))
            .count();
        metrics.actorProcesses = dfd.getNodes()
            .stream()
            .filter(CharacterizedActorProcess.class::isInstance)
            .count();

        return metrics;
    }

}
