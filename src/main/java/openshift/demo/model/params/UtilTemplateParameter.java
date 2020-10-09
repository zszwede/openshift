package openshift.demo.model.params;

import lombok.Builder;

@Builder
public class UtilTemplateParameter {
    public String name;
    public String value;
    public boolean required;
}
