
/*
 * Devloper: Raghul Subramaniam
 * Student ID: 121101340
 * Application: 'Quick Assist' to resolve queries, post information and updated with other locality neighbors 
 */

package com.raghul.manet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manet {

	String netId = null;
	String name = null;
	public Map<String, Node> nodeIpMap = new HashMap<String, Node>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNetId() {
		return netId;
	}

	public void setNetId(String newNetId) {
		this.netId = newNetId;
	}

}
