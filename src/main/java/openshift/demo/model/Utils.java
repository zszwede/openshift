package openshift.demo.model;

import com.google.gson.*;
import com.openshift.internal.restclient.model.*;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.template.Template;
import com.openshift.internal.restclient.model.volume.PersistentVolumeClaimVolumeSource;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IPort;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.IService;
import com.openshift.restclient.model.deploy.DeploymentTriggerType;
import com.openshift.restclient.model.route.IRoute;
import openshift.demo.model.nodes.ContainerModelNode;
import openshift.demo.model.nodes.DeploymentModelNode;
import openshift.demo.model.nodes.GenericModelNode;
import openshift.demo.model.nodes.TemplateModelNode;
import openshift.demo.model.objects.*;
import openshift.demo.model.params.*;
import org.jboss.dmr.ModelNode;

import java.lang.reflect.Type;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    static Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new DoubleSerializer()).setPrettyPrinting().create();


    private static class DoubleSerializer implements JsonSerializer<Double> {
        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            return src == src.longValue() ? new JsonPrimitive(src.longValue()) : new JsonPrimitive(src);
        }
    }

    public static Service createService(String app, String name, List<UtilServicePort> ports, IClient client){
        GenericModelNode smn = new GenericModelNode(ResourceKind.SERVICE);
        ModelNode node = ModelNode.fromJSONString(gson.toJson(smn));
        Map<String, String[]> propertyKeys = ResourcePropertiesRegistry.getInstance().get(smn.apiVersion, ResourceKind.SERVICE);
        Service svc = new Service(node, client, propertyKeys);
        svc.setName(name);
        svc.addLabel("app", app);
        svc.setSelector("app", app);
        ports.forEach(p -> svc.addPort(p.port, p.targetPort, p.name));
        return svc;
    }

    public static Route createRoute(String app, String name, UtilRouteDetails routeDetails, IClient client){
        GenericModelNode smn = new GenericModelNode(ResourceKind.ROUTE);
        ModelNode node = ModelNode.fromJSONString(gson.toJson(smn));
        Map<String, String[]> propertyKeys = ResourcePropertiesRegistry.getInstance().get(smn.apiVersion, ResourceKind.ROUTE);
        Route route = new Route(node, client, propertyKeys);
        route.setName(name);
        route.addLabel("app", app);
        route.setServiceName(routeDetails.svcName);
        route.setHost("routeDetails.host");
        route.createPort().setTargetPort(routeDetails.port);
        return route;
    }

    public static DeploymentConfig createDeployment(String app, String name, IClient client, UtilDeploymentStrategy strategy, List<UtilVolumeMount> mounts, List<UtilServicePort> ports, UtilResources resources){
        Map<Object,Object> sss = gson.fromJson(strategy.toString(), Map.class);
        DeploymentModelNode dcnf = new DeploymentModelNode(sss);
        ModelNode node = ModelNode.fromJSONString(gson.toJson(dcnf));
        Map<String, String[]> propertyKeys = ResourcePropertiesRegistry.getInstance().get(dcnf.apiVersion, ResourceKind.ROUTE);
        DeploymentConfig deployment = new DeploymentConfig(node, client, propertyKeys);
        deployment.setName(name);
        deployment.addLabel("app", app);
        deployment.addTrigger(DeploymentTriggerType.CONFIG_CHANGE);
        deployment.setDesiredReplicaCount(1);
        HashMap<String, String> replicaSelectors = new HashMap<>();
        replicaSelectors.put("app", app);
        replicaSelectors.put("deploymentconfig", app);
        deployment.setReplicaSelector(replicaSelectors);
        for (UtilVolumeMount mount : mounts) {
            PersistentVolumeClaimVolumeSource pvc = new PersistentVolumeClaimVolumeSource(mount.name);
            pvc.setClaimName(mount.pvc);
            deployment.addVolume(pvc);
        }
        return deployment;
    }


    public static String createAll(Application app, IClient client){
        app.container.mounts.stream().forEach(m -> m.name = UUID.randomUUID().toString());
        Container container = createContainer(app.container);
        List<IService> services = app.services.stream().map(s -> createService(container.getName(),s,client)).collect(Collectors.toList());
        List<IRoute> routes = app.routes.stream().map(r -> createRoute(app.details, container.getName(), r, client)).collect(Collectors.toList());
        DeploymentConfig deploymentConfig = createDeployment(app, client);
        deploymentConfig.setContainers(Arrays.asList(container));
        List<IResource> list = new ArrayList<>();
        list.add(deploymentConfig);
        list.addAll(services);
        list.addAll(routes);
        Template template = createTemplate(container.getName(), new ArrayList<>(), list, client);
        return template.toJson();
    }


    public static Template createTemplate(String name, List<UtilTemplateParameter> params, List<IResource> resources, IClient client){
        TemplateModelNode tmn = new TemplateModelNode(params);
        tmn.objects = resources.stream().map(r -> gson.fromJson(r.toJson(), Map.class)).collect(Collectors.toList());
        ModelNode node = ModelNode.fromJSONString(gson.toJson(tmn));
        //String list = resources.stream().map(JSONSerializeable::toJson).collect(Collectors.joining(","));
        //ModelNode model = ModelNode.fromJSONString("[" + list + "]");
        //node.add("objects", model);
        Map<String, String[]> propertyKeys = ResourcePropertiesRegistry.getInstance().get(tmn.apiVersion, ResourceKind.TEMPLATE);
        Template template = new Template(node, client, propertyKeys);
        template.setName(name);
        return template;
    }

    public static DeploymentConfig createDeployment(Application application, IClient client){
        UtilDeploymentStrategy strategy = UtilDeploymentStrategy.builder()
                .type("Rolling")
                .intervalSeconds(1)
                .maxSurge("25%")
                .maxUnavailable("25%")
                .timeoutSeconds(600)
                .updatePeriodSeconds(1)
                .build();
        String app = application.container.app;
        Map<Object,Object> sss = gson.fromJson(strategy.toString(), Map.class);
        DeploymentModelNode dcnf = new DeploymentModelNode(sss);
        ModelNode node = ModelNode.fromJSONString(gson.toJson(dcnf));
        Map<String, String[]> propertyKeys = ResourcePropertiesRegistry.getInstance().get(dcnf.apiVersion, ResourceKind.DEPLOYMENT_CONFIG);
        DeploymentConfig deployment = new DeploymentConfig(node, client, propertyKeys);
        deployment.setName(app);
        deployment.addLabel("app", app);
        deployment.addTrigger(DeploymentTriggerType.CONFIG_CHANGE);
        deployment.setDesiredReplicaCount(1);
        HashMap<String, String> replicaSelectors = new HashMap<>();
        replicaSelectors.put("app", app);
        replicaSelectors.put("deploymentconfig", app);
        deployment.setReplicaSelector(replicaSelectors);
        for (UtilVolumeMount mount : application.container.mounts) {
            PersistentVolumeClaimVolumeSource pvc = new PersistentVolumeClaimVolumeSource(mount.name);
            pvc.setClaimName(mount.pvc);
            deployment.addVolume(pvc);
        }
        return deployment;
    }

    public static Route createRoute(Details det, String app, XRoute xr, IClient client){
        GenericModelNode smn = new GenericModelNode(ResourceKind.ROUTE);
        ModelNode node = ModelNode.fromJSONString(gson.toJson(smn));
        Map<String, String[]> propertyKeys = ResourcePropertiesRegistry.getInstance().get(smn.apiVersion, ResourceKind.ROUTE);
        Route route = new Route(node, client, propertyKeys);
        route.setName(xr.name);
        route.addLabel("app", app);
        route.setServiceName(xr.routeDetails.svcName);
        route.setHost(String.format("%s.%s.%s.nam.nsroot.net", app, det.project, det.cluster));
        route.createPort().setTargetPort(xr.routeDetails.port);
        return route;
    }

    public static Service createService(String app, XService svc, IClient client){
        GenericModelNode smn = new GenericModelNode(ResourceKind.SERVICE);
        ModelNode node = ModelNode.fromJSONString(gson.toJson(smn));
        Map<String, String[]> propertyKeys = ResourcePropertiesRegistry.getInstance().get(smn.apiVersion, ResourceKind.SERVICE);
        Service service = new Service(node, client, propertyKeys);
        service.setName(svc.name);
        service.addLabel("app", app);
        service.setSelector("app", app);
        svc.ports.forEach(p -> service.addPort(p.port, p.targetPort, p.name));
        return service;
    }

    public static Container createContainer(XContainer xc){
        ContainerModelNode cm = new ContainerModelNode();
        cm.volumeMounts = xc.mounts.stream().map(m -> UtilVolumeMount.builder().sub(m.sub).name(m.name).dir(m.dir).build()).collect(Collectors.toList());
        Container c = new Container(ModelNode.fromJSONString(gson.toJson(cm)));
        c.setLimitsCPU(xc.resources.limitCPU);
        c.setRequestsCPU(xc.resources.reqCPU);
        c.setLimitsMemory(xc.resources.limitMemory);
        c.setRequestsMemory(xc.resources.reqMemory);
        Set<IPort> pports = xc.ports.stream().map(port -> new Port(ModelNode.fromJSONString(String.format("{\"containerPort\": %d,\"protocol\": \"TCP\"}", port.port)))).collect(Collectors.toSet());
        c.setPorts(pports);
        xc.envVariables.forEach(ev -> c.addEnvVar(ev.key, ev.val));
        c.setImage(new DockerImageURI(xc.image));
        c.setImagePullPolicy("Always");
        c.setName(xc.app);
        return c;
    }


}
