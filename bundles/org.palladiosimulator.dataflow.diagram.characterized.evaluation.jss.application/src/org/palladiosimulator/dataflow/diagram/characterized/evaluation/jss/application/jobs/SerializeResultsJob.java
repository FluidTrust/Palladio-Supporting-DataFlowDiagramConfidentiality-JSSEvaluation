package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.XtextResource;
import org.palladiosimulator.dataflow.diagram.DataFlowDiagram.DataFlowDiagram;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards.MapHoldingMDSDBlackboard.MapValueLocation;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.Metrics;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.DataDictionaryCharacterized;
import org.palladiosimulator.supporting.prolog.api.PrologAPI;
import org.palladiosimulator.supporting.prolog.model.prolog.PrologFactory;
import org.palladiosimulator.supporting.prolog.model.prolog.Term;
import org.prolog4j.IProverFactory;

import de.uka.ipd.sdq.workflow.jobs.AbstractBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.ModelLocation;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;

public class SerializeResultsJob<T extends MapHoldingMDSDBlackboard> extends AbstractBlackboardInteractingJob<T> {

    private final File destinationFolder;
    private final ModelLocation dfdLocation;
//    private final MapValueLocation queryHelperLocation;
    private final MapValueLocation queryLocation;
    private final MapValueLocation executableProgramLocation;
    private final MapValueLocation solutionLocation;
    private final MapValueLocation resultMetricsLocation;
    private final IProverFactory proverFactory;
    private final PrologAPI prologAPI;

    public SerializeResultsJob(PrologAPI prologAPI, IProverFactory proverFactory, ModelLocation dfdLocation,
            MapValueLocation queryHelperLocation, MapValueLocation queryLocation,
            MapValueLocation executableProgramLocation, MapValueLocation solutionLocation,
            MapValueLocation resultMetricsLocation, File destinationDir) {
        this.proverFactory = proverFactory;
        this.dfdLocation = dfdLocation;
//        this.queryHelperLocation = queryHelperLocation;
        this.queryLocation = queryLocation;
        this.executableProgramLocation = executableProgramLocation;
        this.solutionLocation = solutionLocation;
        this.resultMetricsLocation = resultMetricsLocation;
        this.destinationFolder = destinationDir;
        this.prologAPI = prologAPI;
    }

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        destinationFolder.mkdirs();

        try {
            serializeModels();
            serializePrologFiles();
            serializeMetrics();
        } catch (Exception e) {
            throw new JobFailedException("Error while serializing results.", e);
        }
    }

    protected void serializeMetrics() throws IllegalArgumentException, IllegalAccessException, IOException {
        List<String> csvLines = new ArrayList<>();

        // header
        csvLines.add(Arrays.asList(Metrics.class.getFields())
            .stream()
            .map(Field::getName)
            .collect(Collectors.joining(";")));

        // content
        Metrics metrics = (Metrics) getBlackboard().getValue(resultMetricsLocation);
        List<String> metricValues = new ArrayList<>();
        for (Field field : Metrics.class.getFields()) {
            metricValues.add(field.get(metrics)
                .toString());
        }
        csvLines.add(metricValues.stream()
            .collect(Collectors.joining(";")));

        // serialization
        String csvText = csvLines.stream()
            .collect(Collectors.joining(System.lineSeparator()));

        // writing
        File csvFile = new File(destinationFolder, "metrics.csv");
        Files.writeString(csvFile.toPath(), csvText);
    }

    protected void serializePrologFiles() throws IOException {
        String queryText = (String) getBlackboard().getValue(queryLocation);
        File queryFile = new File(destinationFolder, "query.pl");
        Files.writeString(queryFile.toPath(), queryText);

//        String queryHelperText = (String) getBlackboard().getValue(queryHelperLocation);
//        File queryHelperFile = new File(destinationFolder, "queryHelper.pl");
//        Files.writeString(queryHelperFile.toPath(), queryHelperText);

        String prologProgramText = (String) getBlackboard().getValue(executableProgramLocation);
        File prologProgramFile = new File(destinationFolder, "program.pl");
        Files.writeString(prologProgramFile.toPath(), prologProgramText);

        @SuppressWarnings("unchecked")
        Collection<Map<String, Object>> queryResult = (Collection<Map<String, Object>>) getBlackboard()
            .getValue(solutionLocation);
        String queryResultText = serializeSolution(queryResult);
        File resultsFile = new File(destinationFolder, "results.pl");
        Files.writeString(resultsFile.toPath(), queryResultText);
    }

    protected String serializeSolution(Collection<Map<String, Object>> queryResult) {
        if (queryResult.isEmpty()) {
            return "false.";
        }

        List<String> solutionStrings = new ArrayList<>();
        for (Map<String, Object> solution : queryResult) {
            List<String> variables = new ArrayList<>();
            for (Entry<String, Object> entry : solution.entrySet()) {
                variables.add(String.format("%s = %s", entry.getKey(), serializeJavaValueToPrologTerm(entry.getValue())));
            }
            String solutionString = variables.stream()
                .collect(Collectors.joining("," + System.lineSeparator()));
            solutionStrings.add(solutionString);
        }
        String solutionString = solutionStrings.stream()
            .collect(Collectors.joining(";" + System.lineSeparator())) + ";" + System.lineSeparator() + "false.";
        return solutionString;
    }

    protected String serializeJavaValueToPrologTerm(Object javaValue) {
        Term prologTerm = (Term) proverFactory.createConversionPolicy()
            .convertObject(javaValue);
        var program = PrologFactory.eINSTANCE.createProgram();
        var rule = PrologFactory.eINSTANCE.createRule();
        program.getClauses()
            .add(rule);
        var head = PrologFactory.eINSTANCE.createCompoundTerm();
        rule.setHead(head);
        head.setValue("test");
        rule.setBody(prologTerm);
        var r = new XtextResource();
        r.getContents()
            .add(program);
        String termString = prologAPI.getSerializer()
            .serialize(prologTerm);
        return termString;

    }

    protected void serializeModels() throws IOException {
        ResourceSetPartition dfdPartition = getBlackboard().getPartition(dfdLocation.getPartitionID());
        dfdPartition.resolveAllProxies();
        List<EObject> rsContents = dfdPartition.getResourceSet()
            .getResources()
            .stream()
            .map(Resource::getContents)
            .map(Collection::iterator)
            .map(Iterator::next)
            .collect(Collectors.toList());
        Collection<EObject> rsContentsCopy = EcoreUtil.copyAll(rsContents);

        Optional<DataFlowDiagram> dfdCopy = rsContentsCopy.stream()
            .filter(DataFlowDiagram.class::isInstance)
            .map(DataFlowDiagram.class::cast)
            .findFirst();
        Optional<DataDictionaryCharacterized> ddCopy = rsContentsCopy.stream()
            .filter(DataDictionaryCharacterized.class::isInstance)
            .map(DataDictionaryCharacterized.class::cast)
            .findFirst();

        ResourceSet rs = new ResourceSetImpl();
        File ddFile = new File(destinationFolder, "dd.xmi");
        Resource ddResource = rs.createResource(URI.createFileURI(ddFile.getAbsolutePath()));
        ddResource.getContents()
            .add(ddCopy.get());
        ddResource.save(Collections.emptyMap());
        File dfdFile = new File(destinationFolder, "dfd.xmi");
        Resource dfdResource = rs.createResource(URI.createFileURI(dfdFile.getAbsolutePath()));
        dfdResource.getContents()
            .add(dfdCopy.get());
        dfdResource.save(Collections.emptyMap());
    }

    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        // nothing to cleanup
    }

    @Override
    public String getName() {
        return "Serialize Results.";
    }

}
