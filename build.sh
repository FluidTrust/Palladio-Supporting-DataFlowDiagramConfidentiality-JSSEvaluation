#!/bin/sh

if ! command -v mvn &> /dev/null
then
    echo "Maven is required to build the evaluation package."
    exit
fi

if ! command -v java &> /dev/null
then
    echo "Java >= 11 is required to build the evaluation package."
    exit
fi

if ! command -v git &> /dev/null
then
    echo "Git is required to build the evaluation package."
    exit
fi

# Build artefacts
mvn clean package -Paggregate
mvn clean package -Paggregated
rm -r target

# Archive source code
mkdir -p target/sources
git clone --depth 1 https://github.com/FluidTrust/Palladio-Supporting-Prolog target/sources/Prolog
git clone --depth 1 https://github.com/FluidTrust/Palladio-Supporting-Prolog4J target/sources/Prolog4J
git clone --depth 1 https://github.com/FluidTrust/Palladio-Supporting-DataFlowDiagram target/sources/DataFlowDiagram
git clone --depth 1 https://github.com/FluidTrust/Palladio-Supporting-DataFlowDiagramConfidentiality target/sources/DataFlowDiagramConfidentiality
git clone --depth 1 https://github.com/FluidTrust/Palladio-Supporting-DataFlowDiagramConfidentiality-JSSEvaluation target/sources/Evaluation

# Archive dependencies (binary)
mkdir -p target/dependencies
mv releng/org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.dependencies/target/build/final/* target/dependencies

# Archive executables
mkdir -p target/executables
mv products/org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.product/target/products/*.zip target/executables

# Archive cases
cp -r cases target/

# Run evaluation
EXECUTABLE=win32/win32/x86_64/eclipsec.exe
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    EXECUTABLE=linux/gtk/x86_64/eclipse
elif [[ "$OSTYPE" == "darwin"* ]]; then
    EXECUTABLE=macosx/cocoa/x86_64/Eclipse.app/Contents/MacOS/eclipse
fi
products/org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.product/target/products/org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.product/$EXECUTABLE -f target/cases -d target/results

# Print message
echo
echo "The results of the evaluation are available in target/results"
echo