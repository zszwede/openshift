package openshift.demo.model.params;

import lombok.Builder;

@Builder
public class UtilDeploymentStrategy {
        public String type;
        public int intervalSeconds;
        public int timeoutSeconds;
        public int updatePeriodSeconds;
        public String maxSurge;
        public String maxUnavailable;

    @Override
    public String toString() {
        return String.format("{\n" +
                "      \"resources\": {},\n" +
                "      \"rollingParams\": {\n" +
                "         \"intervalSeconds\": \"%d\",\n" +
                "         \"maxSurge\": \"%s\",\n" +
                "         \"maxUnavailable\": \"%s\",\n" +
                "         \"timeoutSeconds\": \"%d\",\n" +
                "         \"updatePeriodSeconds\": \"%d\"\n" +
                "      },\n" +
                "      \"type\": \"%s\"\n" +
                "}",intervalSeconds, maxSurge, maxUnavailable, timeoutSeconds, updatePeriodSeconds, type);
    }
}



