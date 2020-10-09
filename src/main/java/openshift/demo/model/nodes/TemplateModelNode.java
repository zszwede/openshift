package openshift.demo.model.nodes;

import com.openshift.restclient.ResourceKind;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import openshift.demo.model.params.UtilTemplateParameter;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TemplateModelNode {
    public final String kind = ResourceKind.TEMPLATE;
    public String apiVersion = "v1";
    @NonNull
    public List<UtilTemplateParameter> parameters;
    public List<Map> objects;
}
