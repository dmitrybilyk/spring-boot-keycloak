package com.baeldung.keycloak;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MockRight {
	private String rightName;
	private String keycloakRightId;
	private String scorecardRightId;

	public MockRight(String rightName, String keycloakRightId, String scorecardRightId) {
		this.rightName = rightName;
		this.keycloakRightId = keycloakRightId;
		this.scorecardRightId = scorecardRightId;
	}
}
