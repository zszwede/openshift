package openshift.demo.model.objects;

import java.util.List;

public class Application {
    public Details details;
    public XContainer container;
    public List<XService> services;
    public List<XRoute> routes;
}
