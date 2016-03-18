/**
 *
 */
package com.my.service;

import com.my.exception.BusinessException;
import com.my.model.Access;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.io.*;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

/**
 * @author I311862
 */
@Service("searchService")
public class ElasticSearchService {

	public void searchViews() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		
		// trust ALL certificates
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			@Override
			public boolean isTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
				return true;
			}
		}).build();

		// skip hostname checks
		HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
		SchemeIOSessionStrategy httpsIOSessionStrategy = new SSLIOSessionStrategy(sslContext, hostnameVerifier);
		
		JestClientFactory factory = new JestClientFactory();
		 factory.setHttpClientConfig(new HttpClientConfig
		                        .Builder("https://elasticsearch-anywhere.mo.sap.corp")
		 						.defaultCredentials("eshop", "eshop")
		 						.sslSocketFactory(sslSocketFactory) // this only affects sync calls
		 						.httpsIOSessionStrategy(httpsIOSessionStrategy) // this only affects async calls
		                        .readTimeout(20000)
		 						.multiThreaded(true)
		                        .build());
		 JestClient client = factory.getObject();
		 
		 
		 String query = "{\n" +
					"	  \"size\": 500,\n" +
					"	  \"sort\": {\n" +
					"	    \"@timestamp\": \"desc\" \n" +
					"	  },\n" +  
					"	  \"query\": {\n" + 
					"	    \"filtered\": { \n" + 
					"	      \"query\": { \n" + 
					"	        \"query_string\": { \n" + 
					"	          \"query\": \"_type:eshop\", \n" + 
					"	          \"analyze_wildcard\": true \n" + 
					"	        } \n" + 
					"	      }, \n" + 
					"	      \"filter\": { \n" + 
					"	        \"bool\": { \n" + 
					"	          \"must\": [ \n" + 
					"	            { \n" + 
					"	              \"range\": { \n" + 
					"	                \"@timestamp\": { \n" + 
					"	                  \"gte\": 1455424013168, \n" + 
					"	                  \"lte\": 1458016013168 \n" + 
					"	                } \n" + 
					"	              } \n" + 
					"	            } \n" + 
					"	          ], \n" + 
					"	          \"must_not\": [] \n" + 
					"	        } \n" + 
					"	      } \n" + 
					"	    } \n" + 
					"	  }, \n" + 
					"		\"aggs\": {" +  
					"		    \"2\": {" + 
					"		      \"date_histogram\": {" + 
					"		        \"field\": \"@timestamp\"," + 
					"		        \"interval\": \"30s\"," + 
					"		        \"pre_zone\": \"+08:00\"," + 
					"		        \"pre_zone_adjust_large_interval\": true," + 
					"		        \"min_doc_count\": 0," + 
					"		        \"extended_bounds\": {" + 
					"		          \"min\": 1458034017425," + 
					"		          \"max\": 1458034917425" + 
					"		        }" + 
					"		      }" + 
					"		    }" + 
					"		  }," + 
					"	  \"fields\": [ \n" + 
					"	    \"*\", \n" + 
					"	    \"_source\" \n" + 
					"	  ], \n" + 
					"	  \"script_fields\": {}, \n" + 
					"	  \"fielddata_fields\": [ \n" + 
					"	    \"@timestamp\", \n" + 
					"	    \"datetime\", \n" + 
					"	    \"context.@timestamp\", \n" + 
					"	    \"context.datetime\" \n" + 
					"	  ] \n" + 
					"	}";

		Search search = new Search.Builder(query)
		                .build();

		SearchResult result = client.execute(search);
		
		if (!result.isSucceeded()){
			System.out.println("query failed");
			System.out.println(result.getErrorMessage());
		}else{
			System.out.println("query success");
			List<Access> articles = result.getSourceAsObjectList(Access.class);
			for (Access access : articles) {
				System.out.println(access.getDocumentId());
				System.out.println(access.getVersion());
				System.out.println(access.getMessage());
			}
		}
		
		
	}
 

}
