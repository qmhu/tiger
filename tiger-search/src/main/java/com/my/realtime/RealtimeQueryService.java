package com.my.realtime;

import com.my.elasticsearch.model.Access;
import com.my.exception.BusinessException;
import com.my.model.ResponseTime;
import com.my.model.ResponseTimeResult;
import com.my.service.ElasticSearchManager;
import com.my.util.Util;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.DateHistogramAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.PercentilesAggregation;
import io.searchbox.params.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by I311862 on 2016/5/20.
 */
@Service("realtimeQueryService")
public class RealtimeQueryService {

    @Autowired
    private ElasticSearchManager elasticSearchManager;

    private List<String> notIncludeDomain = new ArrayList<String>();

    public RealtimeQueryService(){
        // cn
        notIncludeDomain.add("csm.sapanywhere.sap.corp");
        notIncludeDomain.add("accounts.sapanywhere.cn");
        notIncludeDomain.add("app1.sapanywhere.cn");
        notIncludeDomain.add("wechat.sapanywhere.cn");
        notIncludeDomain.add("api.sapanywhere.cn");
        notIncludeDomain.add("app1.sapanywhere.sap.corp");
        notIncludeDomain.add("mp.sapanywhere.cn");
        notIncludeDomain.add("dev.sapanywhere.cn");
        notIncludeDomain.add("my.sapanywhere.cn");
        notIncludeDomain.add("bss.sapanywhere.cn");
        notIncludeDomain.add("eap-occ-cn.sapanywhere.cn");
        notIncludeDomain.add("idp.sapanywhere.sap.corp:443");
        notIncludeDomain.add("channel.sapanywhere.cn");
        notIncludeDomain.add("eap-cn.sapanywhere.cn");
        notIncludeDomain.add("static.sapanywhere.cn");
        notIncludeDomain.add("idp.sapanywhere.sap.corp");
        notIncludeDomain.add("doc.sapanywhere.cn");
        notIncludeDomain.add("posmy.sapanywhere.cn");
        notIncludeDomain.add("eap-idp-cn.sapanywhere.cn");

        // us
        notIncludeDomain.add("bss-us.sapanywhere.com");
        notIncludeDomain.add("bss-us.sapanywhere.sap.corp");
        notIncludeDomain.add("doc-us.sapanywhere.com");
        notIncludeDomain.add("doc-us.sapanywhere.sap.corp");
        notIncludeDomain.add("occ1.sapanywhere.com");
        notIncludeDomain.add("occ1.sapanywhere.sap.corp");
        notIncludeDomain.add("idp.sapanywhere.com");
        notIncludeDomain.add("idp.sapanywhere.sap.corp");
        notIncludeDomain.add("csm.sapanywhere.com");
        notIncludeDomain.add("csm.sapanywhere.sap.corp");
        notIncludeDomain.add("doc-us.sapanywhere.com");
        notIncludeDomain.add("doc-us.sapanywhere.sap.corp");
        notIncludeDomain.add("dev-us.sapanywhere.com");
        notIncludeDomain.add("dev-us.sapanywhere.sap.corp");
        notIncludeDomain.add("api-us.sapanywhere.com");
        notIncludeDomain.add("api-us.sapanywhere.sap.corp");
        notIncludeDomain.add("eap-us-mp.sapanywhere.com");
        notIncludeDomain.add("eap-us-mp.sapanywhere.sap.corp");
        notIncludeDomain.add("poseap-us.sapanywhere.com");
        notIncludeDomain.add("poseap-us.sapanywhere.sap.corp");
        notIncludeDomain.add("eap-payment-us.sapanywhere.com");
        notIncludeDomain.add("eap-payment-us.sapanywhere.sap.corp");
        notIncludeDomain.add("eap-occ-us.sapanywhere.com");
        notIncludeDomain.add("eap-occ-us.sapanywhere.sap.corp");
        notIncludeDomain.add("eap-us.sapanywhere.com");
        notIncludeDomain.add("eap-us.sapanywhere.sap.corp");
        notIncludeDomain.add("eap-idp-us.sapanywhere.com");
        notIncludeDomain.add("eap-idp-us.sapanywhere.sap.corp");
        notIncludeDomain.add("app1-us.sapanywhere.com");
        notIncludeDomain.add("accounts-us.sapanywhere.com");
        notIncludeDomain.add("my-us.sapanywhere.com");
        notIncludeDomain.add("mp-us.sapanywhere.com");
        notIncludeDomain.add("stock.sapanywhere.sap.corp:443");
    }

    public ResponseTimeResult realtimeQueryService(String landscape, String[] domainList, String requestType, Long start, Long end, String interval) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        ResponseTimeResult responseTimeResult = new ResponseTimeResult();

        String queryString = "";
        if (interval == null){
            interval = "1h";
        }

        if (domainList != null){
            for(int i=0;i<domainList.length;i++){
                if (i != 0){
                    queryString += " OR ";
                }
                queryString += " http_host:\\\"" + domainList[i] +"\\\"";

            }
        }else{
            for(int i=0;i<notIncludeDomain.size();i++){
                if (i != 0){
                    queryString += " AND ";
                }
                queryString += "NOT http_host:\\\"" + notIncludeDomain.get(i) +"\\\"";
            }
        }

        if (landscape != null){
            queryString += " AND landscape:\\\"" + landscape + "\\\"";
        }

        queryString += " AND NOT request_uri.raw:*\\\\.css* AND NOT request_uri.raw:*\\\\.less* AND NOT request_uri.raw:*\\\\.js AND NOT request_uri.raw:*\\\\.js\\\\?* AND NOT request_uri.raw:*\\\\.png* AND NOT request_uri.raw:*\\\\.gif* AND NOT request_uri.raw:*\\\\.jpg AND NOT request_uri.raw:*\\\\.jpeg AND NOT request_uri.raw:*\\\\.woff* AND NOT request_uri.raw:*\\\\.loc* AND NOT request_uri.raw:*options\\\\.json*";

        if (start == null){
            start = Util.getBeginDateForDay(10).getTime();
        }

        if(end == null){
            end = new Date().getTime();
        }

        String query = "{\n" +
                "	  \"sort\": {\n" +
                "	    \"@timestamp\": \"desc\" \n" +
                "	  },\n" +
                "	  \"query\": {\n" +
                "	    \"filtered\": { \n" +
                "            \"query\": { \n" +
                "                \"query_string\": { \n" +
                "                    \"query\": \"" + queryString + "\", \n" +
                "                    \"analyze_wildcard\": true \n" +
                "                } \n" +
                "            }, \n" +
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
                "	                  \"gte\": " + start.toString() +", \n" +
                "	                  \"lte\": " + end.toString() + " \n" +
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
                "		    \"date_interval\": { \n" +
                "		      \"date_histogram\": { \n" +
                "		        \"field\": \"@timestamp\", \n" +
                "		        \"interval\": \"" + interval + "\", \n" +
                "		        \"time_zone\": \"Asia/Shanghai\", \n" +
                "		        \"min_doc_count\": 0\n" +
                "		      },\n" +
                "           \"aggs\" : { \n" +
                "               \"percentiles_access\" : { \n" +
                "                   \"percentiles\": { \"field\": \"float1\", \"percents\" : [95, 99] } \n" +
                "                } \n" +
                "              } \n" +
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
                "	    \"@timestamp\" \n" +
                "	  ] \n" +
                "	}";

        System.out.println(query);
        Search search = new Search.Builder(query)
                .setParameter(Parameters.SIZE, 0)
                .build();

        SearchResult result = elasticSearchManager.getJestClient().execute(search);

        if (!result.isSucceeded()){
            throw new BusinessException("query elastic search failed:" + result.getErrorMessage());
        }else{
            System.out.println("query success");
            List<Access> articles = result.getSourceAsObjectList(Access.class);
            for (Access access : articles) {
                System.out.println(access.getRequestUri());
            }

            MetricAggregation metricAggregations = result.getAggregations();
            DateHistogramAggregation dateHistogram = metricAggregations.getDateHistogramAggregation("date_interval");
            for (DateHistogramAggregation.DateHistogram dateHis : dateHistogram.getBuckets()){
                System.out.println(dateHis.getTimeAsString() + "   " + dateHis.getCount());

                ResponseTime responseTime = new ResponseTime();
                responseTime.setDate(dateHis.getTimeAsString());
                PercentilesAggregation percentilesAggregation = dateHis.getPercentilesAggregation("percentiles_access");
                for(Map.Entry<String,Double> entry : percentilesAggregation.getPercentiles().entrySet()){
                    System.out.println(entry.getKey() + "   " + entry.getValue());
                    if (entry.getKey().equals("95.0")){
                        responseTime.setTime(String.valueOf(Double.valueOf(entry.getValue()).doubleValue()*1000));
                    }
                }
                responseTimeResult.getResponseTimes().add(responseTime);
            }
        }


        return responseTimeResult;
    }

}
