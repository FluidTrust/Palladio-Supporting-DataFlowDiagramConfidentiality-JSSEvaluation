package org.palladiosimulator.dataflow.diagram.characterized.evaluation.jss.application.blackboards;

import java.util.HashMap;
import java.util.Map;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class MapHoldingMDSDBlackboard extends MDSDBlackboard {

    public static class MapValueLocation {
        private final String partitionId;
        private final String key;

        public MapValueLocation(String partitionId, String key) {
            super();
            this.partitionId = partitionId;
            this.key = key;
        }

        public String getPartitionId() {
            return partitionId;
        }

        public String getKey() {
            return key;
        }

    }

    private final Map<String, Map<String, Object>> partitionMaps = new HashMap<>();

    public Map<String, Object> getPartitionMap(String partitionId) {
        partitionMaps.putIfAbsent(partitionId, new HashMap<String, Object>());
        return partitionMaps.get(partitionId);
    }

    public Object getValue(MapValueLocation location) {
        return getPartitionMap(location.getPartitionId()).get(location.getKey());
    }

    public void addValue(MapValueLocation location, Object value) {
        getPartitionMap(location.getPartitionId()).put(location.key, value);
    }

}
