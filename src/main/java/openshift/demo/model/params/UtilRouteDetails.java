package openshift.demo.model.params;

import lombok.Builder;

@Builder
public class UtilRouteDetails {
    public int port;
    public String svcName;
}
