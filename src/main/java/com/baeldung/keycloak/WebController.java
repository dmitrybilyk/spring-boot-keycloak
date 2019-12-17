package com.baeldung.keycloak;

import com.zoomint.keycloak.clientTokenProvider.ClientTokenProperties;
import com.zoomint.keycloak.clientTokenProvider.ClientTokenProvider;
import com.zoomint.keycloak.provider.api.client.KeycloakApiProviderClient;
import com.zoomint.keycloak.provider.api.client.exceptions.KeycloakApiProviderClientException;
import com.zoomint.keycloak.provider.api.dto.User;
import com.zoomint.keycloak.provider.api.dto.UserLookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

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
    public String test(Principal principal, Model model) throws KeycloakApiProviderClientException {
        ClientTokenProperties clientTokenProperties = ClientTokenProperties.builder().serverUrl(authServerUrl).realm(realm).clientId(keycloakClient).clientSecret(clientSecret).build();
        ClientTokenProvider clientTokenProvider = new ClientTokenProvider(clientTokenProperties);
        get("/", (req, res) -> clientTokenProvider.getAccessTokenString());
        KeycloakApiProviderClient keycloakApiProviderClient = new KeycloakApiProviderClient("http", serverIp, 80, realm);
        List<User> userList = keycloakApiProviderClient.getUsers(clientTokenProvider.getAccessTokenString(), UserLookup.builder().build());
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
