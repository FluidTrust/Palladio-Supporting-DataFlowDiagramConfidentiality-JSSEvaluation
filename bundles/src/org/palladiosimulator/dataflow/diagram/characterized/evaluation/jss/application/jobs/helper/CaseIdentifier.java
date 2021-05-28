package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper;

import java.io.File;
import java.util.Optional;

@FunctionalInterface
public interface CaseIdentifier {

    Optional<Case> findCase(File folder);
    
}
