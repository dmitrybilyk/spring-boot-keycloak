package com.baeldung.keycloak;

import com.zoomint.keycloak.clientTokenProvider.ClientTokenProperties;
import com.zoomint.keycloak.clientTokenProvider.ClientTokenProvider;
import com.zoomint.keycloak.provider.api.client.KeycloakApiProviderClient;
import com.zoomint.keycloak.provider.api.client.exceptions.KeycloakApiProviderClientException;
import com.zoomint.keycloak.provider.api.dto.Group;
import com.zoomint.keycloak.provider.api.dto.GroupLookup;
import com.zoomint.keycloak.provider.api.dto.User;
import com.zoomint.keycloak.provider.api.dto.UserLookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class WebController {

    @Value("${server-ip}")
    private String serverIp;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String keycloakClient;

    @Value("${keycloak-client-token-provider.clientSecret}")
    private String clientSecret;

    @Autowired
    private CustomerDAO customerDAO;

    @GetMapping(path = "/")
    public String index() {
        return "external";
    }

    @GetMapping(path = "/test")
    public String test(Model model) throws KeycloakApiProviderClientException {
        ClientTokenProperties clientTokenProperties = ClientTokenProperties.builder().serverUrl(authServerUrl).realm(realm).clientId(keycloakClient).clientSecret(clientSecret).build();
        ClientTokenProvider clientTokenProvider = new ClientTokenProvider(clientTokenProperties);
//        get("/", (req, res) -> clientTokenProvider.getAccessTokenString());
        KeycloakApiProviderClient keycloakApiProviderClient = new KeycloakApiProviderClient("http", serverIp, 80, realm);

//        List<Group> groups = keycloakApiProviderClient.getGroups(clientTokenProvider.getAccessTokenString(), GroupLookup.builder().build());
//        List<Group> subGroups = keycloakApiProviderClient.getSubGroups(clientTokenProvider.getAccessTokenString(), "80ef4a8b-eb37-4ebf-a1c0-9ce6a108842e");
//        List<Group> subGroups = keycloakApiProviderClient.getSubGroups(clientTokenProvider.getAccessTokenString(), "1f3c6472-bc2b-44fc-ae9a-841c13d09a67");
//        Map<String, List<Group>> groupsMembership = keycloakApiProviderClient.getGroupsMembership(clientTokenProvider.getAccessTokenString(), Collections.singletonList("1"));

//        SupervisorsJpaEntityApiProviderClient

        List<User> userList = keycloakApiProviderClient.getUsers(clientTokenProvider.getAccessTokenString(), UserLookup.builder().username("ablackman").build());

        model.addAttribute("users", userList);
        return "test";
    }

    @GetMapping(path = "/customers")
    public String customers(Principal principal, Model model) {
        addCustomers();
        Iterable<Customer> customers = customerDAO.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("username", principal.getName());
        return "customers";
    }

    // add customers for demonstration
    public void addCustomers() {

        Customer customer1 = new Customer();
        customer1.setAddress("1111 foo blvd");
        customer1.setName("Foo Industries");
        customer1.setServiceRendered("Important services");
        customerDAO.save(customer1);

        Customer customer2 = new Customer();
        customer2.setAddress("2222 bar street");
        customer2.setName("Bar LLP");
        customer2.setServiceRendered("Important services");
        customerDAO.save(customer2);

        Customer customer3 = new Customer();
        customer3.setAddress("33 main street");
        customer3.setName("Big LLC");
        customer3.setServiceRendered("Important services");
        customerDAO.save(customer3);
    }
}
