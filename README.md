# Palladio-Supporting-DataFlowDiagramConfidentiality-JSSEvaluation

This repository contains the scripts and applications to replicate the results of the evaluation of the extended DFD syntax and semantics proposed in a JSS 2021 paper.

## Execution
You can run the `build.sh` to execute the following steps:
* archive all cases (`target/cases`)
* archive all required repositories from Github (`target/sources`)
* collect all dependencies and archive them (`target/dependencies`)
* build the evaluation application (`target/executables`)
* run the evaluation application (`target/results`)

You can also pick individual tasks from the build script and run them manually.

## Project Structure
The `build.sh` script executes the evaluation.

The `cases` folder contains the evaluated cases. For each case, there is a DFD with and one DFD without an issue. Additionally, the queries and visualizations of the DFD are given.

The Maven description (`pom.xml`) and the remaining folders contain the evaluation application that reads the DFDs, transforms the into Prolog programs, runs the analysis and stores the query results. For the cases using the TravelPlanner, ContactSMS and DistanceTracker systems, the Jaccard Coefficient is calculated in addition.