package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ConventionalCaseIdentifier implements CaseIdentifier {

    @FunctionalInterface
    interface CaseFieldSetter {
        default void setField(Case foundCase, File file) {
            setField(foundCase, file, file.getName());
        }

        void setField(Case foundCase, File file, String fileName);
    }

    private static final Iterable<CaseFieldSetter> SETTERS = createFieldSetters();

    @Override
    public Optional<Case> findCase(File folder) {
        if (folder != null && folder.isDirectory() && folder.exists()) {
            return findCaseInFolder(folder);
        }
        return Optional.empty();
    }

    private static Iterable<CaseFieldSetter> createFieldSetters() {
        Collection<CaseFieldSetter> setters = new ArrayList<>();

        setters.add((c, f, n) -> {
            if (n.endsWith(".xmi") && n.contains("dfd") && !n.contains("violation")) {
                c.setDfdModel(f);
            }
        });

        setters.add((c, f, n) -> {
            if (n.endsWith(".xmi") && n.contains("dfd") && n.contains("violation")) {
                c.setDfdViolationModel(f);
            }
        });

        setters.add((c, f, n) -> {
            if (n.endsWith(".pl") && n.contains("query")) {
                c.setQueryFile(f);
            }
        });
        
        setters.add((c,f,n) -> {
            if (n.endsWith(".drawio") || n.endsWith(".pdf") || n.endsWith(".svg")) {
                c.addAuxiliarFile(f);
            }
        });

        return Collections.unmodifiableCollection(setters);
    }

    protected Optional<Case> findCaseInFolder(File folder) {
        // identify case data
        Case foundCase = new Case();
        foundCase.setCaseName(folder.getName());
        for (String filename : folder.list()) {
            SETTERS.forEach(s -> s.setField(foundCase, new File(folder, filename)));
        }

        if (foundCase.isValid()) {
            return Optional.of(foundCase);
        }
        return Optional.empty();
    }

}
