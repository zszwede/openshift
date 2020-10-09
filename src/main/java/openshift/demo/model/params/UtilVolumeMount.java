package openshift.demo.model.params;

import lombok.Builder;

@Builder
public class UtilVolumeMount {
    public String name;
    public String pvc;
    public String dir;
    public String sub;

}
