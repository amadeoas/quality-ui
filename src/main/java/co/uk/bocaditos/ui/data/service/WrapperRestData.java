package co.uk.bocaditos.ui.data.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import co.uk.bocaditos.ui.views.RestInfo;


/**
 * .
 *
 * @author aasco
 */
public class WrapperRestData<Q> {

	private final HttpMethod method;
	private final URI uri;
	private final MultiValueMap<String, String> headers;
	private final Q request;


	public WrapperRestData(final String method, final Map<String, String> headers, 
			final String uri, final Map<String, String> urlParams, final Q request) 
			throws URISyntaxException {
		this.method = buildMethod(method);
		this.headers = buildHeaders(headers);
		this.uri = buildUri(uri, urlParams);
		this.request = request;
	}

	public WrapperRestData(final RestInfo<Q> info) throws URISyntaxException {
		this(info.getMethod(), info.getHeaders(), info.getUri(), info.getUrlParams(), 
				info.getRequest());
	}
	
	public HttpMethod getMethod() {
		return this.method;
	}
	
	public MultiValueMap<String, String> getHeaders() {
		return this.headers;
	}

	public URI getUri() {
		return this.uri;
	}

	public Q getRequest() {
		return this.request;
	}
	
	public <R> ResponseEntity<R> exchange(final RestTemplate restTemplate, 
			final Class<R> responseClass) {
		final RequestEntity<Q> requestEntity 
				= new RequestEntity<>(this.request, this.headers, this.method, this.uri);

		return restTemplate.exchange(this.uri, this.method, requestEntity, responseClass);
	}
	
	private static HttpMethod buildMethod(final String method) {
		return HttpMethod.valueOf(method);
	}
	
	private static MultiValueMap<String, String> buildHeaders(final Map<String, String> headers) {
		final HttpHeaders h = new HttpHeaders();

		if (headers != null) {
			h.setAll(headers);
		}

		return h;
	}
	
	private static URI buildUri(String uri, final Map<String, String> urlParams) 
			throws URISyntaxException {
		if (urlParams == null || urlParams.size() == 0) {
			return new URI(uri);
		}
		
		final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);

		return builder.buildAndExpand(urlParams).toUri();
	}

} // end class WrapperRestData
