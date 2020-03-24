package it.paolodedo.reactivespringdataelasticsearch.model;

public class MyModel {

	private String id;

	private Long timestamp;

	private String data;

	public MyModel() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Model{" +
			"id='" + id + '\'' +
			", timestamp=" + timestamp +
			", data='" + data + '\'' +
			'}';
	}
}
