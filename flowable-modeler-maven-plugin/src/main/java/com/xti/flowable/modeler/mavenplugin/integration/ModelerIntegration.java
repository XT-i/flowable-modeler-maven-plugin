package com.xti.flowable.modeler.mavenplugin.integration;

import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.maven.plugin.logging.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.xti.flowable.modeler.mavenplugin.common.ModelFileStatus;
import com.xti.flowable.modeler.mavenplugin.integration.json.CreateModelResponse;
import com.xti.flowable.modeler.mavenplugin.integration.json.GetModelsResponse;

public class ModelerIntegration {

	private static String FLOWABLE_REMEMBER_ME_COOKIE = "FLOWABLE_REMEMBER_ME=";

	private Log log;
	private String url;

	private String sessionCookie;

	public ModelerIntegration(Log log, String url) {
		this.log = log;
		this.url = url;

		log.info("Flowable @ " + url);
	}

	public void login(String username, String password) {
		log.info("Logging in with username '" + username + "'");

		StringBuffer endpointAuthenticate = new StringBuffer();
		endpointAuthenticate.append(url);
		endpointAuthenticate.append("/flowable-idm/app/authentication");

		log.debug("  authentication endpoint: " + endpointAuthenticate);

		endpointAuthenticate.append("?j_username=");
		endpointAuthenticate.append(username);
		endpointAuthenticate.append("&j_password=");
		endpointAuthenticate.append(password);
		endpointAuthenticate.append("&_spring_security_remember_me=true&submit=Login");

		HttpResponse<String> result;
		try {
			result = Unirest.post(endpointAuthenticate.toString()).asString();

			log.debug("http status: " + result.getStatus());
			log.debug("result headers: " + result.getHeaders());

			List<String> cookieHeaders = result.getHeaders().get("Set-Cookie");
			if ( cookieHeaders != null ) {
				for (String cookieHeader : cookieHeaders) {
					if (cookieHeader.startsWith(FLOWABLE_REMEMBER_ME_COOKIE)) {
						this.sessionCookie = cookieHeader.substring(FLOWABLE_REMEMBER_ME_COOKIE.length(),
								cookieHeader.indexOf(";"));
						break;
					}
				}
			}
			
			if ( sessionCookie == null ) {
				throw new RuntimeException("login to Flowable Modeler @ " + url + " failed.");
			}

			log.debug("session cookie: '" + sessionCookie + "'");
		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}

	public GetModelsResponse getProcesses() {
		StringBuffer endpointGetProcesses = new StringBuffer();
		endpointGetProcesses.append(url);
		endpointGetProcesses.append("/flowable-modeler/app/rest/models?filter=processes&modelType=0&sort=modifiedDesc");

		log.debug("  getProcesses endpoint: " + endpointGetProcesses);

		try {
			HttpResponse<String> processes = Unirest.get(endpointGetProcesses.toString())
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie).asString();

			log.debug("http status: " + processes.getStatus());
			log.debug("body: '" + processes.getBody());

			ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
			GetModelsResponse gpr = null;
			try {
				gpr = om.readValue(processes.getBody(), GetModelsResponse.class);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			return gpr;

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}

	public String getProcessModel(String id) {
		StringBuffer endpointGetProcess = new StringBuffer();
		endpointGetProcess.append(url);
		endpointGetProcess.append("/flowable-modeler/app/rest/models/" + id + "/bpmn20");

		log.debug("  getProcess endpoint: " + endpointGetProcess);

		try {
			HttpResponse<String> model = Unirest.get(endpointGetProcess.toString())
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie).asString();

			log.debug("http status: " + model.getStatus());

			return model.getBody();

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}
	
	public ModelFileStatus importProcess ( String content ) {
		ModelFileStatus result = ModelFileStatus.UNKNOWN;
		
		StringBuffer endpoint = new StringBuffer();
		endpoint.append(url);
		endpoint.append("/flowable-modeler/app/rest/import-process-model");

		log.debug("  importProcess endpoint: " + endpoint);

		try {
			String boundary = "----SomeMagicBoundary";
			
			StringBuffer data = new StringBuffer();
			data.append("--");
			data.append(boundary);
			data.append("\r\n");
			data.append("Content-Disposition: form-data; name=\"file\"; filename=\"modelFile.bpmn20.xml\"\r\n");
			data.append("Content-Type: text/xml\r\n\r\n");
			data.append(content);
			data.append("\r\n\r\n--");
			data.append(boundary);
			data.append("--");

			HttpResponse<String> response = Unirest.post(endpoint.toString())
					.header("Content-Type", "multipart/form-data; boundary=" + boundary)
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie)
					.body(data.toString())
					.asString();

			log.debug("http status: " + response.getStatus());

			if ( response.getStatus() == HttpStatus.SC_OK ) {
				result = ModelFileStatus.IMPORTED;
			} else {
				log.debug(response.getBody());
				result = ModelFileStatus.ERROR;
			}
			
		} catch (UnirestException e) {
			log.error(e);
			result = ModelFileStatus.ERROR;
		}
		
		return result;

	}

	public GetModelsResponse getCases() {
		StringBuffer endpointGetProcesses = new StringBuffer();
		endpointGetProcesses.append(url);
		endpointGetProcesses.append("/flowable-modeler/app/rest/models?filter=processes&modelType=5&sort=modifiedDesc");

		log.debug("  getCases endpoint: " + endpointGetProcesses);

		try {
			HttpResponse<String> processes = Unirest.get(endpointGetProcesses.toString())
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie).asString();

			log.debug("http status: " + processes.getStatus());
			log.debug("body: '" + processes.getBody());

			ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
			GetModelsResponse gcr = null;
			try {
				gcr = om.readValue(processes.getBody(), GetModelsResponse.class);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			return gcr;

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}

	public String getCaseModel(String id) {
		StringBuffer endpoint = new StringBuffer();
		endpoint.append(url);
		endpoint.append("/flowable-modeler/app/rest/models/" + id + "/cmmn");

		log.debug("  getCase endpoint: " + endpoint);

		try {
			HttpResponse<String> model = Unirest.get(endpoint.toString())
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie).asString();

			log.debug("http status: " + model.getStatus());

			return model.getBody();

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}
	

	public ModelFileStatus importCaseModel ( String content ) {
		ModelFileStatus result = ModelFileStatus.UNKNOWN;
		
		StringBuffer endpoint = new StringBuffer();
		endpoint.append(url);
		endpoint.append("/flowable-modeler/app/rest/import-case-model");

		log.debug("  importCase endpoint: " + endpoint);

		try {
			String boundary = "----SomeMagicBoundary";
			
			StringBuffer data = new StringBuffer();
			data.append("--");
			data.append(boundary);
			data.append("\r\n");
			data.append("Content-Disposition: form-data; name=\"file\"; filename=\"modelFile.cmmn.xml\"\r\n");
			data.append("Content-Type: text/xml\r\n\r\n");
			data.append(content);
			data.append("\r\n\r\n--");
			data.append(boundary);
			data.append("--");

			HttpResponse<String> response = Unirest.post(endpoint.toString())
					.header("Content-Type", "multipart/form-data; boundary=" + boundary)
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie)
					.body(data.toString())
					.asString();

			log.debug("http status: " + response.getStatus());

			if ( response.getStatus() == HttpStatus.SC_OK ) {
				result = ModelFileStatus.IMPORTED;
			} else {
				log.debug(response.getBody());
				result = ModelFileStatus.ERROR;
			}

		} catch (UnirestException e) {
			log.error(e);
			result = ModelFileStatus.ERROR;
		}
		
		return result;

	}
	
	public GetModelsResponse getDecisions() {
		StringBuffer endpoint = new StringBuffer();
		endpoint.append(url);
		endpoint.append("/flowable-modeler/app/rest/models?filter=processes&modelType=4&sort=modifiedDesc");

		log.debug("  getDecisions endpoint: " + endpoint);

		try {
			HttpResponse<String> processes = Unirest.get(endpoint.toString())
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie).asString();

			log.debug("http status: " + processes.getStatus());
			log.debug("body: '" + processes.getBody());

			ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
			GetModelsResponse gcr = null;
			try {
				gcr = om.readValue(processes.getBody(), GetModelsResponse.class);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			return gcr;

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}

	public String getDecisionModel(String id) {
		StringBuffer endpoint = new StringBuffer();
		endpoint.append(url);
		endpoint.append("/flowable-modeler/app/rest/decision-table-models/" + id + "/export");

		log.debug("  getDecision endpoint: " + endpoint);

		try {
			HttpResponse<String> model = Unirest.get(endpoint.toString())
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie).asString();

			log.debug("http status: " + model.getStatus());

			return model.getBody();

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}
	

	public ModelFileStatus importDecisionModel ( String content ) {
		ModelFileStatus result = ModelFileStatus.UNKNOWN;
		
		StringBuffer endpoint = new StringBuffer();
		endpoint.append(url);
		endpoint.append("/flowable-modeler/app/rest/decision-table-models/import-decision-table");

		log.debug("  importDecision endpoint: " + endpoint);

		try {
			String boundary = "----SomeMagicBoundary";
			
			StringBuffer data = new StringBuffer();
			data.append("--");
			data.append(boundary);
			data.append("\r\n");
			data.append("Content-Disposition: form-data; name=\"file\"; filename=\"modelFile.dmn.xml\"\r\n");
			data.append("Content-Type: text/xml\r\n\r\n");
			data.append(content);
			data.append("\r\n\r\n--");
			data.append(boundary);
			data.append("--");

			HttpResponse<String> response = Unirest.post(endpoint.toString())
					.header("Content-Type", "multipart/form-data; boundary=" + boundary)
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie)
					.body(data.toString())
					.asString();

			log.debug("http status: " + response.getStatus());

			if ( response.getStatus() == HttpStatus.SC_OK ) {
				result = ModelFileStatus.IMPORTED;
			} else {
				log.debug(response.getBody());
				result = ModelFileStatus.ERROR;
			}

		} catch (UnirestException e) {
			log.error(e);
			result = ModelFileStatus.ERROR;
		}
		
		return result;

	}
	
	
	public GetModelsResponse getForms() {
		StringBuffer endpoint = new StringBuffer();
		endpoint.append(url);
		endpoint.append("/flowable-modeler/app/rest/models?filter=processes&modelType=2&sort=modifiedDesc");

		log.debug("  getForms endpoint: " + endpoint);

		try {
			HttpResponse<String> processes = Unirest.get(endpoint.toString())
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie).asString();

			log.debug("http status: " + processes.getStatus());
			log.debug("body: '" + processes.getBody());

			ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
			GetModelsResponse gcr = null;
			try {
				gcr = om.readValue(processes.getBody(), GetModelsResponse.class);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			return gcr;

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}

	public String getFormModel(String id) {
		StringBuffer endpoint = new StringBuffer();
		endpoint.append(url);
		endpoint.append("/flowable-modeler/app/rest/form-models/" + id);

		log.debug("  getFormModel endpoint: " + endpoint);

		try {
			HttpResponse<JsonNode> model = Unirest.get(endpoint.toString())
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie)
					.asJson();

			log.debug("http status: " + model.getStatus());
			
			String result = null;
			try {
				ObjectMapper om = new ObjectMapper();
				result = om.readTree(model.getBody().toString()).get("formDefinition").toString();
			} catch (JsonMappingException e) {
				throw new RuntimeException(e);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			
			return result;

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
	}

	public ModelFileStatus importFormModel ( String content ) {
		ModelFileStatus result = ModelFileStatus.UNKNOWN;
		
		
		ReadContext readContext = JsonPath.parse(content);
		Object key = readContext.read("$.key");
		Object name = readContext.read("$.name");
		
		try {
			/// CREATE EMPTY FORM MODEL
			
			StringBuffer endpointCreate = new StringBuffer();
			endpointCreate.append(url);
			endpointCreate.append("/flowable-modeler/app/rest/models");
			
			log.debug("  importFormCreate endpoint: " + endpointCreate);
			
			String createData = "{\"name\":\"" + name + "\",\"key\":\"" + key + "\",\"description\":\"\",\"modelType\":2}";

			HttpResponse<String> response = Unirest.post(endpointCreate.toString())
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("Accept", "application/json")
					.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie)
					.body(createData)
					.asString();

			log.debug("http status: " + response.getStatus());


			if ( response.getStatus() == HttpStatus.SC_OK ) {
				result = ModelFileStatus.UNKNOWN;
			} else {
				log.error(response.getBody());
				result = ModelFileStatus.ERROR;
			}

			if ( result != ModelFileStatus.ERROR ) {
				
				ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
				CreateModelResponse cmr = null;
				try {
					cmr= om.readValue(response.getBody(), CreateModelResponse.class);
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
	
				// UPDATE WITH FORM DATA
				
				StringBuffer endpoint = new StringBuffer();
				endpoint.append(url);
				endpoint.append("/flowable-modeler/app/rest/form-models/");
				endpoint.append(cmr.getId());
				
				log.debug("  importFormUpdate endpoint: " + endpoint);
	
				String data = "{\"reusable\":false,\"newVersion\":false,\"comment\":\"\",\"formRepresentation\":{\"id\":\"" + cmr.getId() + "\",\"name\":\"" + name + "\",\"key\":\"" + key + "\",\"description\":\"\",\"version\":1,\"lastUpdatedBy\":\"admin\",\"lastUpdated\":\"2020-02-10T12:07:03.153+0000\",\"formDefinition\":"
						 + content
						 + "}, \"formImageBase64\":\"data:image/png;base64,\"}";
						 
				HttpResponse<String> updateResponse = Unirest.put(endpoint.toString())
						.header("Content-Type", "application/json;charset=UTF-8")
						.header("Accept", "application/json")
						.header("Set-Cookie", FLOWABLE_REMEMBER_ME_COOKIE + sessionCookie)
						.body(data.toString())
						.asString();
	
				log.debug("http status: " + updateResponse.getStatus());
				log.debug(updateResponse.getBody());
	
				if ( updateResponse.getStatus() == HttpStatus.SC_OK ) {
					result = ModelFileStatus.IMPORTED;
				} else {
					log.debug(updateResponse.getBody());
					result = ModelFileStatus.ERROR;
				}
			}
			
		} catch (UnirestException e) {
			log.error(e);
			result = ModelFileStatus.ERROR;
		}
		
		return result;

	}
	
}
