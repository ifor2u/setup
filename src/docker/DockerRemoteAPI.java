package docker;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DockerRemoteAPI {
	private String dockerUrl = "${docker.url}";

	public static void main(String[] args) {
		DockerRemoteAPI api = new DockerRemoteAPI();
		ObjectMapper mapper = new ObjectMapper();
		Docker docker = new Docker();
		try {
			String image = "crosbymichael/dockerui";
			String name = "test";
			docker.setImage(image);

			String json = mapper.writeValueAsString(docker);
			System.out.println(json);
			String result = api.create(name, json);
			System.out.println("RESULT:" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String create(String name, String json) {
		String url = dockerUrl + "/containers/create";
		String result = "";
		if (name != null) {
			url += "?name=" + name;
		}
		try {
			HttpClient client = HttpClients.createDefault();
			HttpPost postMethod = new HttpPost(url);
			postMethod.setEntity(new StringEntity(json));

			HttpResponse response = client.execute(postMethod);
			result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String ps() {
		String result = "";
		try {
			HttpClient client = HttpClients.createDefault();
			HttpGet getMethod = new HttpGet(dockerUrl + "/containers/json");
			HttpResponse response = client.execute(getMethod);
			result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

class Docker {
	private String hostname = "";
	private String user = "";
	private List<String> cmd = new ArrayList<String>();
	private String image = "";
	private String name = "";

	@JsonProperty(value = "Name")
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@JsonProperty(value = "Hostname")
	public String getHostname() {
		return hostname;
	}

	@JsonProperty(value = "User")
	public String getUser() {
		return user;
	}

	@JsonProperty(value = "Image")
	public String getImage() {
		return image;
	}

	@JsonProperty(value = "Cmd")
	public List<String> getCmd() {
		return cmd;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setCmd(List<String> cmd) {
		this.cmd = cmd;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
