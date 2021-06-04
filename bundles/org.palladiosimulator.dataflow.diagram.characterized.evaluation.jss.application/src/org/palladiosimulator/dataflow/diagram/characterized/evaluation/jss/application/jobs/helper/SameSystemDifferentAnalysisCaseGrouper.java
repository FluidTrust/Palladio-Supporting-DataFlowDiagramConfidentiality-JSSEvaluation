package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SameSystemDifferentAnalysisCaseGrouper implements CaseGrouper {

    @Override
    public Collection<CaseGroup> groupCases(Collection<Case> cases) {
        Map<Object, List<Case>> caseGroups = cases.stream()
            .collect(Collectors.groupingBy(c -> c.getAuxiliarFiles()
                .stream()
                .map(File::getName)
                .filter(name -> name.endsWith(".pdf"))
                .map(name -> name.replace(".pdf", ""))
                .findFirst()
                .orElse(c.getCaseName())));
        return caseGroups.entrySet()
            .stream()
            .map(e -> new CaseGroup((String) e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }

}
