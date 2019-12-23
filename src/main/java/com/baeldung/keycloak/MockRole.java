package com.baeldung.keycloak;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MockRole {
	private String roleName;
	private String keycloakRoleId;
	private String scorecardRoleId;
}
