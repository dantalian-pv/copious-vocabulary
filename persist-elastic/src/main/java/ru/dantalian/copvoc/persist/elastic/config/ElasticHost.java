package ru.dantalian.copvoc.persist.elastic.config;

public class ElasticHost {

	private String host;

	private Integer port;

	private String scheme;

	public ElasticHost() {
	}

	public ElasticHost(final String aHost, final Integer aPort, final String aScheme) {
		host = aHost;
		port = aPort;
		scheme = aScheme;
	}

	public String getHost() {
		return host;
	}

	public void setHost(final String aHost) {
		host = aHost;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(final Integer aPort) {
		port = aPort;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(final String aScheme) {
		scheme = aScheme;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ElasticHost)) {
			return false;
		}
		final ElasticHost other = (ElasticHost) obj;
		if (host == null) {
			if (other.host != null) {
				return false;
			}
		} else if (!host.equals(other.host)) {
			return false;
		}
		if (port == null) {
			if (other.port != null) {
				return false;
			}
		} else if (!port.equals(other.port)) {
			return false;
		}
		if (scheme == null) {
			if (other.scheme != null) {
				return false;
			}
		} else if (!scheme.equals(other.scheme)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ElasticHost [host=" + host + ", port=" + port + ", scheme=" + scheme + "]";
	}

}
