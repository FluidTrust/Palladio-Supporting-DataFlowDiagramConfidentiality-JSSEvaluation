package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.jobs.helper;

import java.util.Collection;

@FunctionalInterface
public interface CaseGrouper {

    public static class CaseGroup {
        private final String groupName;
        private final Collection<Case> cases;

        public CaseGroup(String groupName, Collection<Case> cases) {
            super();
            this.groupName = groupName;
            this.cases = cases;
        }

        public String getGroupName() {
            return groupName;
        }

        public Collection<Case> getCases() {
            return cases;
        }

    }

    Collection<CaseGroup> groupCases(Collection<Case> cases);

}
