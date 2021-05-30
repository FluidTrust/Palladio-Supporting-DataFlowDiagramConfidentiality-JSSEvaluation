package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class Case {

    private String caseName;
    private File dfdModel;
    private File dfdViolationModel;
    private Optional<File> queryHelperFile = Optional.empty();
    private File queryFile;
    private Collection<File> auxiliarFiles = new ArrayList<>();

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public File getDfdModel() {
        return dfdModel;
    }

    public void setDfdModel(File dfdModel) {
        this.dfdModel = dfdModel;
    }

    public File getDfdViolationModel() {
        return dfdViolationModel;
    }

    public void setDfdViolationModel(File dfdViolationModel) {
        this.dfdViolationModel = dfdViolationModel;
    }

    public Optional<File> getQueryHelperFile() {
        return queryHelperFile;
    }

    public void setQueryHelperFile(File queryHelperFile) {
        this.queryHelperFile = Optional.ofNullable(queryHelperFile);
    }

    public File getQueryFile() {
        return queryFile;
    }

    public void setQueryFile(File queryFile) {
        this.queryFile = queryFile;
    }

    public void addAuxiliarFile(File file) {
        this.auxiliarFiles.add(file);
    }

    public Collection<File> getAuxiliarFiles() {
        return Collections.unmodifiableCollection(this.auxiliarFiles);
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(caseName) && dfdModel != null && dfdModel.exists() && dfdViolationModel != null
                && dfdViolationModel.exists() && queryFile != null && queryFile.exists()
                && queryHelperFile.map(File::exists)
                    .orElse(true);
    }

}
