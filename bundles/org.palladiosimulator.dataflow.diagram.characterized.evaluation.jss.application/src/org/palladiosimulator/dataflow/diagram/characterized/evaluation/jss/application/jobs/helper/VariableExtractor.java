package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper;

import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.Activator;
import org.palladiosimulator.supporting.prolog.model.prolog.CompoundTerm;
import org.palladiosimulator.supporting.prolog.model.prolog.expressions.Expression;
import org.palladiosimulator.supporting.prolog.parser.antlr.PrologParser;

public final class VariableExtractor {
    
    private VariableExtractor() {
        // intentionally left blank
    }

    public static Collection<String> determineVariables(String goal) {
        PrologParser parser = Activator.getInstance()
            .getPrologAPI()
            .getParser();
        var initialRule = parser.getGrammarAccess()
            .getExpression_1100_xfyRule();
        Expression parsedQuery;
        try (StringReader sr = new StringReader(goal)) {
            var parsingResult = parser.parse(initialRule, sr);
            parsedQuery = (Expression) parsingResult.getRootASTElement();
        }

        var variables = new LinkedHashSet<String>();
        var queue = new LinkedList<EObject>();
        queue.add(parsedQuery);
        while (!queue.isEmpty()) {
            EObject currentElement = queue.pop();
            queue.addAll(currentElement.eContents());
            if (currentElement instanceof CompoundTerm) {
                var term = (CompoundTerm) currentElement;
                if (term.getArguments()
                    .isEmpty()) {
                    variables.add(term.getValue());
                }
            }
        }
        return variables;
    }
    
}
