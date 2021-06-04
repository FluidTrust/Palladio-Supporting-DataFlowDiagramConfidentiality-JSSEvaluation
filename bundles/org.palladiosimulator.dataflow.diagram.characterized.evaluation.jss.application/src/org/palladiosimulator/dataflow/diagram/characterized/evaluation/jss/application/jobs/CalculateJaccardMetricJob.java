package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.util.CompareSwitch;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper.Case;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import de.uka.ipd.sdq.workflow.jobs.AbstractJob;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

public class CalculateJaccardMetricJob extends AbstractJob {

    protected static class ChangedElementsLeftFinder extends CompareSwitch<Collection<EObject>> {

        @Override
        public Collection<EObject> caseReferenceChange(ReferenceChange object) {
            Set<EObject> changes = new HashSet<>(caseDiff(object));
            if (object.getKind() == DifferenceKind.ADD && object.getSource() == DifferenceSource.LEFT) {
                changes.add(object.getValue());
            }
            return changes;
        }

        @Override
        public Collection<EObject> caseDiff(Diff object) {
            return Arrays.asList(object.getMatch()
                .getLeft());
        }

    }

    protected static class ChangedElementsRightFinder extends CompareSwitch<Collection<EObject>> {

        @Override
        public Collection<EObject> caseReferenceChange(ReferenceChange object) {
            Set<EObject> changes = new HashSet<>(caseDiff(object));
            if (object.getKind() == DifferenceKind.DELETE && object.getSource() == DifferenceSource.LEFT) {
                changes.add(object.getValue());
            }
            return changes;
        }

        @Override
        public Collection<EObject> caseDiff(Diff object) {
            return Arrays.asList(object.getMatch()
                .getRight());
        }

    }

    protected static final EMFCompare COMPARATOR = createComparator();
    protected static final ChangedElementsLeftFinder CHANGED_ELEMENTS_FINDER_LEFT = new ChangedElementsLeftFinder();
    protected static final ChangedElementsRightFinder CHANGED_ELEMENTS_FINDER_RIGHT = new ChangedElementsRightFinder();
    protected final Collection<Case> cases;
    protected File destinationFile;

    public CalculateJaccardMetricJob(Collection<Case> cases, File destinationFile) {
        if (cases.size() != 2) {
            throw new IllegalArgumentException(
                    "The " + CalculateJaccardMetricJob.class + " does only support exactly two cases.");
        }
        this.cases = cases;
        this.destinationFile = destinationFile;
    }

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        // initialize resource sets
        List<ResourceSet> resourceSets = new ArrayList<>();
        for (Case caseToExecute : cases) {
            ResourceSet rs = new ResourceSetImpl();
            URI uri = URI.createFileURI(caseToExecute.getDfdModel()
                .getAbsolutePath());
            rs.getResource(uri, true);
            EcoreUtil.resolveAll(rs);
            resourceSets.add(rs);
        }

        IComparisonScope comparisonScope = new DefaultComparisonScope(resourceSets.get(0), resourceSets.get(1), null);
        Comparison comparison = COMPARATOR.compare(comparisonScope);

        // identify changes
        Set<EObject> changed0 = new HashSet<>();
        Set<EObject> changed1 = new HashSet<>();
        for (Diff diff : comparison.getDifferences()) {
            changed0.addAll(CHANGED_ELEMENTS_FINDER_LEFT.doSwitch(diff));
            changed1.addAll(CHANGED_ELEMENTS_FINDER_RIGHT.doSwitch(diff));
        }
        changed1.remove(null);
        changed0.remove(null);

        // build element sets for metric calculation
        Set<EObject> contents0 = getAllContents(resourceSets.get(0));
        Set<EObject> contents1 = getAllContents(resourceSets.get(1));
        SetView<EObject> unchanged0 = Sets.difference(contents0, changed0);
        SetView<EObject> unchanged1 = Sets.difference(contents1, changed1);
        if (unchanged0.size() != unchanged1.size()) {
            throw new IllegalStateException("The size of unchanged elements should be the same.");
        }

        // calculate metric
        int numIntersection = unchanged0.size();
        int numUnion = (contents0.size() - numIntersection) + (contents1.size() - numIntersection) + numIntersection;
        double jaccard = numIntersection / (double) numUnion;

        // serialize results
        List<String> resultLines = new ArrayList<>();
        resultLines.add(String.format("Jaccard coefficient:\t%d/%d = %.2f", numIntersection, numUnion, jaccard));
        resultLines.add("\tunchanged0: " + getTypeNames(unchanged0));
        resultLines.add("\tunchanged1: " + getTypeNames(unchanged1));
        resultLines.add("\tchanged0: " + getTypeNames(changed0));
        resultLines.add("\tchanged1: " + getTypeNames(changed1));
        String resultString = resultLines.stream()
            .collect(Collectors.joining(System.lineSeparator()));
        try {
            Files.writeString(destinationFile.toPath(), resultString);
        } catch (IOException e) {
            throw new JobFailedException("Could not write results.", e);
        }
    }

    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        // nothing to cleanup
    }

    @Override
    public String getName() {
        return "Calculate Jaccard Metric";
    }

    protected static EMFCompare createComparator() {
        IEObjectMatcher fallBackMatcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
        IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
        IMatchEngine.Factory.Registry registry = MatchEngineFactoryRegistryImpl.createStandaloneInstance();
        @SuppressWarnings("deprecation")
        final MatchEngineFactoryImpl matchEngineFactory = new MatchEngineFactoryImpl(fallBackMatcher,
                comparisonFactory);
        matchEngineFactory.setRanking(20);
        registry.add(matchEngineFactory);
        Logger.getLogger(EMFCompare.class.getPackageName())
            .setLevel(Level.WARN);
        return EMFCompare.builder()
            .setMatchEngineFactoryRegistry(registry)
            .build();
    }

    protected static Set<EObject> getAllContents(ResourceSet rs) {
        Set<EObject> contents = new HashSet<>();
        rs.getAllContents()
            .forEachRemaining(n -> {
                if (n instanceof EObject) {
                    contents.add((EObject) n);
                }
            });
        return contents;
    }

    protected static String getTypeNames(Collection<EObject> unchanged) {
        return unchanged.stream()
            .map(EObject::eClass)
            .map(EClass::getName)
            .distinct()
            .sorted()
            .collect(Collectors.joining(", "));
    }
}
