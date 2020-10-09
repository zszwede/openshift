package openshift.demo.model.nodes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenericModelNode {
    @NonNull
    public String kind;
    public String apiVersion = "v1";
}
