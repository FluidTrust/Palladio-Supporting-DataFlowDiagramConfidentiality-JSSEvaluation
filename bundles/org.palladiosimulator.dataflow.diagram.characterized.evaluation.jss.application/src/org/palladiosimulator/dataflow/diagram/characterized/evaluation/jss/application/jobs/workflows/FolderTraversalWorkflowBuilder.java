package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.workflows;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.palladiosimulator.dataflow.diagram.characterized.DataFlowDiagramCharacterized.DataFlowDiagramCharacterizedPackage;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.CalculateJaccardMetricJob;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.CaseJob;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.Case;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.CaseGrouper;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.CaseGrouper.CaseGroup;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.ConventionalCaseIdentifier;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.SameSystemDifferentAnalysisCaseGrouper;
import org.palladiosimulator.dataflow.dictionary.characterized.DataDictionaryCharacterized.DataDictionaryCharacterizedPackage;

import de.uka.ipd.sdq.workflow.Workflow;
import de.uka.ipd.sdq.workflow.jobs.SequentialJob;

public final class FolderTraversalWorkflowBuilder {

    private FolderTraversalWorkflowBuilder() {
        // intentionally left blank
    }

    public static Workflow createWorkflow(File baseFolder, File destinationFolder) throws IOException {
        SequentialJob jobSequence = new SequentialJob("Folder-based evaluation");

        EcorePlugin.ExtensionProcessor.process(null);
        DataFlowDiagramCharacterizedPackage.eINSTANCE.eClass();
        DataDictionaryCharacterizedPackage.eINSTANCE.eClass();

        // jobs for individual cases
        ConventionalCaseIdentifier caseIdentifier = new ConventionalCaseIdentifier();
        Collection<Case> foundCases = findCasesByConvention(baseFolder, caseIdentifier);
        for (Case foundCase : foundCases) {
            jobSequence.add(new CaseJob(foundCase, destinationFolder));
        }

        // jobs for case pairs
        CaseGrouper sameSystemCaseGroups = new SameSystemDifferentAnalysisCaseGrouper();
        Collection<CaseGroup> caseGroups = sameSystemCaseGroups.groupCases(foundCases)
            .stream()
            .filter(c -> c.getCases()
                .size() > 1)
            .collect(Collectors.toList());
        for (CaseGroup caseGroup : caseGroups) {
            File destinationFile = new File(destinationFolder, caseGroup.getGroupName() + "_jaccard.txt");
            jobSequence.add(new CalculateJaccardMetricJob(caseGroup.getCases(), destinationFile));
        }

        return new Workflow(jobSequence);
    }

    protected static Collection<Case> findCasesByConvention(File baseFolder, ConventionalCaseIdentifier caseIdentifier)
            throws IOException {
        Collection<Case> foundCases = new ArrayList<>();
        Files.walkFileTree(baseFolder.toPath(), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Optional<Case> foundCase = caseIdentifier.findCase(dir.toFile());
                if (foundCase.isPresent()) {
                    foundCases.add(foundCase.get());
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

        });
        return foundCases;
    }

}
