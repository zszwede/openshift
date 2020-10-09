package openshift.demo.controlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.openshift.restclient.IClient;
import openshift.demo.model.Utils;
import openshift.demo.model.objects.Application;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.Mockito.mock;

@RestController
public class MainControler {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @PostMapping("/graph")
    public String greeting(@RequestBody Application application) {
        IClient client = mock(IClient.class);
        String ttt = Utils.createAll(application, client);
        try {
            String graph = asYaml(ttt);
            System.out.println(graph);
            return graph;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String asYaml(String jsonString) throws JsonProcessingException, IOException {
        // parse JSON
        JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
        // save it as YAML
        String jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
        return jsonAsYaml;
    }
}