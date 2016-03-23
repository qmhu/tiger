/**
 *
 */
package com.my.service;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.my.elasticsearch.model.Access;
import com.my.exception.BusinessException;

import com.my.mongo.model.EshopMeta;
import com.my.mongo.model.EshopAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * @author I311862
 */
@Service("searchService")
public class SearchService {

	@Autowired
	private ElasticSearchManager elasticSearchManager;
	private List<String> notIncludeDomain;

	@Autowired
	private MongoDBClient mongoDBClient;


	public SearchService() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		notIncludeDomain = new ArrayList<String>();
		/*notIncludeDomain.add("channel.sapanywhere.cn");
		notIncludeDomain.add("eap-occ-us.sapanywhere.com");
		notIncludeDomain.add("accounts-eu.sapanywhere.com");
		notIncludeDomain.add("mp-eu.sapanywhere.com");
		notIncludeDomain.add("eap-idp-us.sapanywhere.com");
		notIncludeDomain.add("occ1.sapanywhere.sap.corp:443");
		notIncludeDomain.add("api-us.sapanywhere.com");
		notIncludeDomain.add("idp.sapanywhere.sap.corp:443");
		notIncludeDomain.add("eap-us.sapanywhere.com");
		notIncludeDomain.add("my-eu.sapanywhere.com");
		notIncludeDomain.add("dev-us.sapanywhere.com");
		notIncludeDomain.add("app1-eu.sapanywhere.com");
		notIncludeDomain.add("accounts-eu.sapanywhere.com");*/
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

	}


	public void searchViews(int dayBefore) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		int totalCount = 0;
		int from = 0;
		int size = 1000;

		totalCount = queryAndSaveToMongoDB(from, size, dayBefore);

		while(totalCount > (from + size)){
			from += size;
			queryAndSaveToMongoDB(from, size, dayBefore);
		}

		System.out.println("Finish import " + totalCount + " eshopAccess." );

	}

	public void deleteMongoDBRecord(int dayBefore){
		dayBefore = -dayBefore;

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, dayBefore);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date dateBegin = calendar.getTime();
		System.out.println(dateBegin);

		calendar.add(calendar.DAY_OF_YEAR, 1);
		Date dateEnd = calendar.getTime();
		System.out.println(dateEnd);

		WriteResult result = mongoDBClient.
				getMongoOperation().
				getCollection("EshopAccess").remove(
					new BasicDBObject("createTime",new BasicDBObject("$gte", dateBegin).append("$lt", dateEnd)));

	}


	private int queryAndSaveToMongoDB(int from, int size, int dayBefore) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		dayBefore = -dayBefore;

		Date createTime = null;

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, dayBefore);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		createTime = calendar.getTime();
		System.out.println(calendar.getTime());
		String yesterdayBegin = String.valueOf(calendar.getTimeInMillis());

		calendar.add(calendar.DAY_OF_YEAR, 1);
		System.out.println(calendar.getTime());
		String yesterdayEnd = String.valueOf(calendar.getTimeInMillis());

		String queryString = "landscape:\\\"cn\\\"";
		for(String notDomain : notIncludeDomain){
			queryString += " AND ";
			queryString += "NOT http_host:\\\"" + notDomain +"\\\"";
		}

		String query = elasticSearchManager.buildEshopQueryString(String.valueOf(from), String.valueOf(size), "desc", queryString, yesterdayBegin,yesterdayEnd);
		System.out.println(query);

		System.out.println("from:" + from + " size:" + size);

		Search search = new Search.Builder(query)
				.build();

		SearchResult result = elasticSearchManager.getJestClient().execute(search);

		if (!result.isSucceeded()){
			throw new BusinessException("query elastic search failed:" + result.getErrorMessage());
		}else{
			System.out.println("query success");
			List<Access> articles = result.getSourceAsObjectList(Access.class);
			for (Access access : articles) {
				System.out.println(access.toString());
				EshopAccess eshopAccess = new EshopAccess(access);
				eshopAccess.setCreateTime(createTime);
				mongoDBClient.getMongoOperation().save(eshopAccess);
			}

			return result.getTotal();
		}
	}


	public void generateMeta() {
		// delete all meta first
		mongoDBClient.
				getMongoOperation().
				getCollection("EshopMeta").remove(
				new BasicDBObject("",""));

		// rebuild EshopMeta meta
		AggregationOutput outputPath = mongoDBClient.getMongoOperation().getCollection("EshopAccess").aggregate(asList(
				new BasicDBObject("$match",
						new BasicDBObject("requestType","doc")),
				new BasicDBObject("$group",
						new BasicDBObject("_id", new BasicDBObject("httpHost", "$httpHost").
								append("contentPath", "$contentPath")))));

		for (final DBObject result: outputPath.results()){
			System.out.println(result);
			BasicDBObject meta = (BasicDBObject) result.get("_id");
			if (meta != null) {
				EshopMeta eshopMeta = new EshopMeta();
				eshopMeta.setContentPath((String)meta.get("contentPath"));
				eshopMeta.setDomain((String)meta.get("httpHost"));
				mongoDBClient.getMongoOperation().save(eshopMeta);
			}
		}


	}
}
