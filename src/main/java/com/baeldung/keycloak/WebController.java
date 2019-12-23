package com.baeldung.keycloak;

import com.baeldung.dao.RoleRepository;
import com.zoomint.keycloak.clientTokenProvider.ClientTokenProperties;
import com.zoomint.keycloak.clientTokenProvider.ClientTokenProvider;
import com.zoomint.keycloak.provider.api.client.KeycloakApiProviderClient;
import com.zoomint.keycloak.provider.api.client.exceptions.KeycloakApiProviderClientException;
import com.zoomint.keycloak.provider.api.dto.Group;
import com.zoomint.keycloak.provider.api.dto.GroupLookup;
import com.zoomint.keycloak.provider.api.dto.User;
import com.zoomint.keycloak.provider.api.dto.UserLookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class WebController {

    @Autowired
    private RoleRepository roleRepository;

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

//        List<Group> subGroups = keycloakApiProviderClient.getSubGroups(clientTokenProvider.getAccessTokenString(), "80ef4a8b-eb37-4ebf-a1c0-9ce6a108842e");
//        List<Group> subGroups = keycloakApiProviderClient.getSubGroups(clientTokenProvider.getAccessTokenString(), "1f3c6472-bc2b-44fc-ae9a-841c13d09a67");
//        Map<String, List<Group>> groupsMembership = keycloakApiProviderClient.getGroupsMembership(clientTokenProvider.getAccessTokenString(), Collections.singletonList("1"));

//        SupervisorsJpaEntityApiProviderClient

//	    1) get all groups
	    List<Group> groups = keycloakApiProviderClient.getGroups(clientTokenProvider.getAccessTokenString(), GroupLookup.builder().fullTextSearch("Group").build());
//
//			2) load all roles (mocks for now)

        MockRole superVisorMockRole = createMockRole("15b4b33f-19e0-41d4-a765-22e0386edf66", "Supervisor", "1");
        MockRole teamLeaderMockRole = createMockRole("37a93839-6b6f-4452-95a2-72397fb18d9d", "Team leader", "3");
        MockRole itAdministratorMockRole = createMockRole("46f21ea9-2355-4d07-ad9e-89c98cfa2ab6", "IT Administrator", "5");
        MockRole ccManagerMockRole = createMockRole("fad2d1c2-edfc-4fa6-a795-b19c0842ef5f", "CC Manager", "2");
        MockRole customerSurveyMockRole = createMockRole("c3f8db91-ae64-448a-9265-74ca3a482032", "Customer Survey", "6");
        MockRole agentMockRole = createMockRole("6294e5aa-481e-4574-a37e-1fab2cab72ac", "Agent", "4");
        MockRole complianceAnalystMockRole = createMockRole("7bedaa09-3b85-46b0-94c2-33b2fefe707d", "Compliance Analyst", "7");

        Map<String, List<MockRight>> roleRightsMap = new HashMap<>();
        List<MockRight> mockRights = Arrays.asList(new MockRight("VIEW_MY_EVALS", "", "4"),
                new MockRight("VIEW_TEAM_EVALS", "", "5"),
                new MockRight("CREATE_EVALS", "", "6"),
                new MockRight("REOPEN_EVALS", "", "7"),
                new MockRight("EVAL_AGENTS", "", "8"),
                new MockRight("REPLACE_CALLS", "", "10"),
                new MockRight("ADD_CALL", "", "11"),
                new MockRight("VIEW_REPORTS", "", "13"),
                new MockRight("RESET_TEAM_PASSWORD", "", "17"),
                new MockRight("PLAN_GROUP_EVALS", "", "23"),
                new MockRight("INTERACTIONS_GROUP_VIEW", "", "31"),
                new MockRight("ADD_AUDIT_REASON", "", "39"),
                new MockRight("REPORTING_ASSIGNED_TEAMS", "", "42"),
                new MockRight("REPORTING_SELF", "", "44"),
                new MockRight("INTERACTION_TAGS_ADD", "", "60"),
                new MockRight("INTERACTION_TAGS_VIEW", "", "62"),
                new MockRight("INTERACTION_REVIEWS_VIEW", "", "63"),
                new MockRight("VIEW_CONVERSATIONS_SCREEN", "", "66"));
        roleRightsMap.put(superVisorMockRole.getRoleName(), mockRights);

//      3) to load all users

        List<User> userList = keycloakApiProviderClient.getUsers(clientTokenProvider.getAccessTokenString(), UserLookup.builder().build());

        userList.forEach(
                user -> { user.getRoles().clear();

                // we set ccmanager role for all users for testing purposes for now
                user.getRoles().add(ccManagerMockRole.getRoleName());

                // we set root gropu as a group every user can evaluate for now
                user.getUsersTeams().add("13ac743e-8562-4c83-be24-9104062e3ff4");
                });
//      4) Match user with groups and roles and save it to db.
        model.addAttribute("users", userList);
        return "test";
    }

    private MockRole createMockRole(String keycloakRoleId, String roleName, String scorecardRoleId) {
        MockRole mockRole = new MockRole();
        mockRole.setKeycloakRoleId(keycloakRoleId);
        mockRole.setRoleName(roleName);
        mockRole.setScorecardRoleId(scorecardRoleId);
        return mockRole;
    }

    //notes
//    1) For rights the 'name'  will be used to match with qm db.
//    1) For roles the 'name'  will be used to match with qm db??? -  it's just a pure string?.

    //todo
	//1) SCOPICS - to add keycloak_user_id to sc_users;
	//2) SCOPICS - to add keycloak_group_id to ccgroups;
  //3) PHEANIX - in user>roles to return just the composite role which is assign to the user
  //3) PHEANIX - in user>usersTeams - to ruturn th elist of groups user can evaluate



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

    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleRepository getRoleRepository() {
        return roleRepository;
    }
}
