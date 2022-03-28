package com.raghul.manet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

@Path("/manet")
public class ManetServices {
	
	@GET
	@Path("/test")
	@Produces("application/json")
	public String sanityTest() {

		return "Hello world , this is a sanity test";

	}

	static String type, user, pass;
	static Map<String, Manet> manetIdMap = new HashMap<String, Manet>();
	static Map<String, Map<String, String>> userLoginMap = new HashMap<String, Map<String, String>>();
	// <device type , <username , password> >
	static int netId = 0;

	@PUT
	@Path("/register")
	@Produces("application/json")
	public String doRegister(@QueryParam("deviceType") String type, @QueryParam("userName") String user,
			@QueryParam("password") String pass) {

		Map<String, String> creds = new HashMap<String, String>();

		this.type = type;
		this.user = user;
		this.pass = pass;

		String pattern = "{ Registered successfully \"Device Type\":\"%s\", \"User Name\":\"%s\", \"Password\": \"%s\"}";
		return String.format(pattern, type, user, pass);

	}

	@PUT
	@Path("/login")
	@Produces("application/json")
	public String doLogin(@QueryParam("userName") String user, @QueryParam("password") String pass) {

		if (user.equalsIgnoreCase(this.user) && pass.equalsIgnoreCase(this.pass)) {
			String pattern = "{ Authentication Successful  \"User Name\":\"%s\", \"Password\": \"%s\"}";
			return String.format(pattern, user, pass);
		} else {
			String pattern = "{ Authentication failed \"User Name\":\"%s\", \"Password\": \"%s\" }";
			return String.format(pattern, user, pass);
		}

	}

	@PUT
	@Path("/createManet")
	@Produces("application/json")
	public String createManet(@QueryParam("networkName") String name) {

		String newNetId = generateNewId();

		Manet newManet = new Manet();
		newManet.setNetId(newNetId);
		newManet.setName(name);

		manetIdMap.put(newNetId, newManet);

		String pattern = "{ New Manet created \"Netwrok Id\":\"%s\"}";
		return String.format(pattern, newNetId);

	}

	@PUT
	@Path("/joinManet")
	@Produces("application/json")
	public String joinManet(@QueryParam("networkId") String netId, @QueryParam("localIp") String localIp,
			@QueryParam("globalIp") String globalIp) {

		Manet currManet = manetIdMap.get(netId);
		Node node = new Node();

		node.setLocal(localIp);
		node.setGlobal(globalIp);

		currManet.nodeIpMap.put(localIp, node);

		String pattern = "{ Successfully joined with Manet \"Netwrok Id\":\"%s\"}";
		return String.format(pattern, netId);

	}

	@PUT
	@Path("/leaveManet")
	@Produces("application/json")
	public String leaveManet(@QueryParam("localIp") String localIp) {

		Manet currManet = manetIdMap.entrySet().iterator().next().getValue();
		currManet.nodeIpMap.remove(localIp);

		String pattern = "{ bye ! left from the Manet \"Netwrok Id\":\"%s\"}";
		return String.format(pattern, currManet.netId);

	}

	@PUT
	@Path("/splitManet")
	@Produces("application/json")
	public String splitManet(@QueryParam("ipList") String ipString) {

		Manet currManet = manetIdMap.entrySet().iterator().next().getValue();
		Manet newManet = new Manet();

		String netId = generateNewId(); // to generate a new ID
		newManet.setNetId(netId);

		// setting a name for the new MANET
		newManet.setName(currManet.getName() + "_split_" + netId);

		List<String> ipList = new ArrayList<String>();
		// lis of the IP's are comma seperated
		ipList = Arrays.asList(ipString.split(","));

		for (String ip : ipList) {
			Node pivotNode = currManet.nodeIpMap.get(ip);
			newManet.nodeIpMap.put(ip, pivotNode);
			currManet.nodeIpMap.remove(ip);
		}

		manetIdMap.put(netId, newManet);

		String pattern = "{ \"Manet\":\"%s\" \"has been splitted to\" :\"%s\" \"and\" :\"%s\" }";
		return String.format(pattern, currManet.getName(), currManet.getName(), newManet.getName());

	}

	@PUT
	@Path("/mergeManet")
	@Produces("application/json")
	public String mergeManet(@QueryParam("netId1") String netId1, @QueryParam("netId2") String netId2) {

		Manet net1, net2;

		net1 = manetIdMap.get(netId1);
		net2 = manetIdMap.get(netId2);

		/*
		 * for (Manet manet : manetList) { if (manet.getNetId() == netId1) { net1 =
		 * manet; } else if (manet.getNetId() == netId2) { net2 = manet; } }
		 */

		for (Map.Entry<String, Node> entry : net1.nodeIpMap.entrySet()) {

			net1.nodeIpMap.put(entry.getKey(), entry.getValue());
		}

		// de-allocating the network 2 from the Network list
		manetIdMap.remove(net2);

		String pattern = "{ \"Manet\":\"%s\" has been merged with Manet \"%s\" }";
		return String.format(pattern, netId2, netId1);

	}

	@GET
	@Path("/listManet")
	@Produces("application/json")
	public String listManet() {
		StringBuilder st = new StringBuilder();

		for (Map.Entry<String, Manet> entry : manetIdMap.entrySet()) {
			int count = 1;
			Manet manet = entry.getValue();

			st.append(new Gson().toJson(manet));
			st.append("\n");
		}

		return st.toString();
	}

	private static String generateNewId() {
		return "000" + ++netId;

	}

}