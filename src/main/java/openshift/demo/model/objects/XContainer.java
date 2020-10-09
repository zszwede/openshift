package openshift.demo.model.objects;

import openshift.demo.model.params.UtilEnvVariable;
import openshift.demo.model.params.UtilResources;
import openshift.demo.model.params.UtilServicePort;
import openshift.demo.model.params.UtilVolumeMount;

import java.util.List;

public class XContainer {
    public String image;
    public String app;
    public List<UtilServicePort> ports;
    public List<UtilVolumeMount> mounts;
    public List<UtilEnvVariable> envVariables;
    public UtilResources resources;
}
