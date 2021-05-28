package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application;

import java.io.File;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.workflows.FolderTraversalWorkflowBuilder;

public class CLI implements IApplication {

    @Override
    public Object start(IApplicationContext context) throws Exception {
        String[] args = Optional.ofNullable(context.getArguments()
            .get(IApplicationContext.APPLICATION_ARGS))
            .filter(String[].class::isInstance)
            .map(String[].class::cast)
            .orElse(new String[0]);

        Options cliOptions = new Options();
        Option folderOption = new Option("f", true, "looks through a folder and executes all found cases");
        Option destinationOption = new Option("d", true, "output folder for results");
        Option helpOption = new Option("h", false, "prints cli help");
        cliOptions.addOption(folderOption);
        cliOptions.addOption(destinationOption);
        cliOptions.addOption(helpOption);
        folderOption.setType(File.class);
        destinationOption.setType(File.class);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(cliOptions, args);

        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        
        if (cmd.hasOption(helpOption.getOpt())) {
            printHelp(cliOptions);
            return IApplication.EXIT_OK;
        } else if (cmd.hasOption(folderOption.getOpt()) && cmd.hasOption(destinationOption.getOpt())) {
            File inputFolder = (File) cmd.getParsedOptionValue(folderOption.getOpt());
            File outputFolder = (File) cmd.getParsedOptionValue(destinationOption.getOpt());
            FolderTraversalWorkflowBuilder.createWorkflow(inputFolder, outputFolder)
                .run();
            return IApplication.EXIT_OK;
        } else {
            System.err.println("The given command line options are invalid.");
            printHelp(cliOptions);
        }

        return -1;
    }

    @Override
    public void stop() {
        // nothing to do here
    }

    protected static void printHelp(Options cliOptions) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Evaluation CLI", cliOptions);
    }

}
