package openshift.demo.model.params;

import lombok.Builder;

@Builder
public class UtilServicePort {
    public String name;
    public Integer port;
    public Integer targetPort;
}
