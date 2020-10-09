package openshift.demo.model.nodes;

import com.openshift.restclient.ResourceKind;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class DeploymentModelNode {
    public final String kind = ResourceKind.DEPLOYMENT_CONFIG;
    public String apiVersion = "v1";
    @NonNull
    public Map<Object,Object> strategy;
}
