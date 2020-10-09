package openshift.demo.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.gson.Gson;
import com.mangofactory.typescript.TypescriptCompiler;
import com.openshift.internal.restclient.model.DeploymentConfig;
import com.openshift.internal.restclient.model.Route;
import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import openshift.demo.model.params.*;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

public class DeploymentTest {
    @Test
    public void createDeployment() {
        IClient client = mock(IClient.class);

        ModelNode node = ModelNode.fromJSONString("{\n" +
                "    \"kind\": \"DeploymentConfig\",\n" +
                "    \"apiVersion\": \"v1\"}");
        Map<String, String[]> propertyKeys = ResourcePropertiesRegistry.getInstance().get("v1", ResourceKind.DEPLOYMENT_CONFIG);
        propertyKeys.put("aaa", new String[]{"dsdsdsd"});
        DeploymentConfig dc = new DeploymentConfig(node, client, propertyKeys);

        System.out.println(dc);

    }

    public Gson gson = new Gson();
    public IClient client = mock(IClient.class);

    public String templateJson = "{\n" +
            "    \"kind\" : \"Template\",\n" +
            "    \"apiVersion\" : \"v1\",\n" +
            "    \"parameters\" : [\n" +
            "        {\n" +
            "            \"name\" : \"A\",\n" +
            "            \"value\" : \"a\",\n" +
            "            \"required\" : true\n" +
            "        },\n" +
            "        {\n" +
            "            \"name\" : \"B\",\n" +
            "            \"value\" : \"b\",\n" +
            "            \"required\" : false\n" +
            "        }\n" +
            "    ],\n" +
            "    \"metadata\" : {\"name\" : \"dipa\"}\n" +
            "}";

    public String serviceJson = "{\n" +
            "    \"kind\" : \"Service\",\n" +
            "    \"apiVersion\" : \"v1\",\n" +
            "    \"metadata\" : {\n" +
            "        \"name\" : \"svc\",\n" +
            "        \"labels\" : {\"app\" : \"APP\"}\n" +
            "    },\n" +
            "    \"spec\" : {\n" +
            "        \"selector\" : {\"app\" : \"APP\"},\n" +
            "        \"ports\" : [\n" +
            "            {\n" +
            "                \"port\" : 1234,\n" +
            "                \"targetPort\" : 1234,\n" +
            "                \"name\" : \"port-1234\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"port\" : 5678,\n" +
            "                \"targetPort\" : 5678,\n" +
            "                \"name\" : \"port-5678\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"port\" : 9012,\n" +
            "                \"targetPort\" : 9012,\n" +
            "                \"name\" : \"port-9012\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    public String routeJson = "{\n" +
            "    \"kind\" : \"Route\",\n" +
            "    \"apiVersion\" : \"v1\",\n" +
            "    \"metadata\" : {\n" +
            "        \"name\" : \"svc\",\n" +
            "        \"labels\" : {\"app\" : \"APP\"}\n" +
            "    },\n" +
            "    \"spec\" : {\n" +
            "        \"to\" : {\n" +
            "            \"name\" : \"svc_name\",\n" +
            "            \"kind\" : \"Service\"\n" +
            "        },\n" +
            "        \"host\" : \"some.host\",\n" +
            "        \"port\" : {\"targetPort\" : 12345}\n" +
            "    }\n" +
            "}";

    @Test
    public void createTemplateTest() {
        List<UtilTemplateParameter> params = Arrays.asList(UtilTemplateParameter.builder().name("A").value("a").required(true).build(),
                UtilTemplateParameter.builder().name("B").value("b").required(false).build());
        //Template tmpl = Utils.createTemplate("dipa", params, client);
        //Assert.assertEquals(tmpl.toPrettyString(), templateJson);
    }

    @Test
    public void createServiceTest() {
        List<UtilServicePort> ports = Stream.of(1234, 5678, 9012).map(p -> UtilServicePort.builder().name(String.format("port-%d", p)).targetPort(p).port(p).build()).collect(Collectors.toList());
        Service svc = Utils.createService("APP", "svc", ports, client);
        Assert.assertEquals(svc.toPrettyString(), serviceJson);
    }

    @Test
    public void createRouteTest() {
        UtilRouteDetails urd = UtilRouteDetails.builder().port(12345).svcName("svc_name").build();

        Route route = Utils.createRoute("APP", "svc", urd, client);
        Assert.assertEquals(route.toPrettyString(), routeJson);
    }

    @Test
    public void createDeploymentTest() {
        UtilDeploymentStrategy strategy = UtilDeploymentStrategy.builder()
                .type("Rolling")
                .intervalSeconds(1)
                .maxSurge("25%")
                .maxUnavailable("25%")
                .timeoutSeconds(600)
                .updatePeriodSeconds(1)
                .build();
        UtilVolumeMount uvm = UtilVolumeMount.builder().dir("/mount/path").name("mount_name").pvc("pvc").sub("./sub/path").build();
        List<UtilServicePort> ports = Stream.of(1234, 5678, 9012).map(p -> UtilServicePort.builder().name(String.format("port-%d", p)).port(p).build()).collect(Collectors.toList());
/*        DeploymentConfig dep = Utils.createDeployment("APP", "svc", client, strategy, Arrays.asList(uvm), ports);
        try{
            String yaml = asYaml(dep.toPrettyString());
            System.out.println(yaml);
        }catch (Exception e){

        }*/
    }

        /*
    strategy:
      resources: {}
      rollingParams:
        intervalSeconds: 1
        maxSurge: 25%
        maxUnavailable: 25%
        timeoutSeconds: 600
        updatePeriodSeconds: 1
      type: Rolling
    * */

    public String asYaml(String jsonString) throws JsonProcessingException, IOException {
        // parse JSON
        JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
        // save it as YAML
        String jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
        return jsonAsYaml;
    }

    @Test
    public void toJS(){
        TypescriptCompiler compiler = new TypescriptCompiler();

        String output = compiler.compile("interface RootObject {\n" +
                "  app: string;\n" +
                "  container: Container;\n" +
                "}\n" +
                "\n" +
                "interface Container {\n" +
                "  name: string;\n" +
                "  ports: Port[];\n" +
                "}\n" +
                "\n" +
                "interface Port {\n" +
                "  name: string;\n" +
                "  port: number;\n" +
                "}");
        
// Compile a string:

        System.out.println(output);
    }

}
