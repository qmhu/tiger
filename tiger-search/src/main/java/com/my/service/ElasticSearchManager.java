package com.my.service;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.stereotype.Service;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by I311862 on 2016/3/18.
 */

@Service("elasticSearchManager")
public class ElasticSearchManager {

    private JestClient client;

    public synchronized JestClient getJestClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        if (client == null){
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
                    .defaultCredentials("eshop", "Initial0!")
                    .sslSocketFactory(sslSocketFactory) // this only affects sync calls
                    .httpsIOSessionStrategy(httpsIOSessionStrategy) // this only affects async calls
                    .readTimeout(200000)
                    .multiThreaded(true)
                    .build());
            client = factory.getObject();
        }

        return client;
    }

    public String buildEshopQueryString(String from, String size, String sort,String queryString, String starttime, String endtime){
        String query = "{\n" +
                "	  \"from\": " + from + ",\n" +
                "	  \"size\": " + size + ",\n" +
                "	  \"sort\": {\n" +
                "	    \"@timestamp\": \"desc\" \n" +
                "	  },\n" +
                "	  \"query\": {\n" +
                "	    \"filtered\": { \n" +
                "	      \"query\": { \n" +
                "	        \"query_string\": { \n" +
                "	          \"query\": \" "+ queryString + "\" ,\n" +
                "	          \"analyze_wildcard\": true \n" +
                "	        } \n" +
                "	      }, \n" +
                "	      \"filter\": { \n" +
                "	        \"bool\": { \n" +
                "	          \"must\": [ \n" +
                "                {\n" +
                "                    \"query\": { \n" +
                "                       \"match\": {\n" +
                "                           \"type\": {\n" +
                "                               \"query\": \"proxy\",\n" +
                "                               \"type\": \"phrase\" \n" +
                "                            \n}" +
                "                           \n}" +
                "                       \n}" +
                "                },\n" +
                "	            { \n" +
                "	              \"range\": { \n" +
                "	                \"@timestamp\": { \n" +
                "	                  \"gte\": " + starttime + ", \n" +
                "	                  \"lte\": " + endtime + " \n" +
                "	                } \n" +
                "	              } \n" +
                "	            } \n" +
                "	          ], \n" +
                "	          \"must_not\": [] \n" +
                "	        } \n" +
                "	      } \n" +
                "	    } \n" +
                "	  }, \n" +
                "		\"aggs\": { \n" +
                "		    \"2\": { \n" +
                "		      \"date_histogram\": { \n" +
                "		        \"field\": \"@timestamp\", \n" +
                "		        \"interval\": \"30s\", \n" +
                "		        \"pre_zone\": \"+08:00\", \n" +
                "		        \"pre_zone_adjust_large_interval\": true, \n" +
                "		        \"min_doc_count\": 0, \n" +
                "		        \"extended_bounds\": { \n" +
                "		          \"min\": 1458034017425, \n" +
                "		          \"max\": 1458034917425 \n" +
                "		        }\n" +
                "		      }\n" +
                "		    }\n" +
                "		  },\n" +
                "	  \"fields\": [ \n" +
                "	    \"*\", \n" +
                "	    \"_source\" \n" +
                "	  ], \n" +
/*                "    \"script_fields\": { \n" +
                "        \"duration_number\": { \n" +
                "            \"script\": \"doc['ReqTime'].value==null?(doc['duration'].value==null?0:Integer.parseInt(doc['duration'].value)):Math.round((int)(100000*Float.parseFloat(doc['ReqTime'].value))/100)\",\n" +
                "                    \"lang\": \"groovy\" \n" +
                "        } \n" +
                "    },\n" +*/
                "	  \"fielddata_fields\": [ \n" +
                "	    \"@timestamp\", \n" +
                "	    \"datetime\", \n" +
                "	    \"context.@timestamp\", \n" +
                "	    \"context.datetime\" \n" +
                "	  ] \n" +
                "	}";

        return query;
    }


}
