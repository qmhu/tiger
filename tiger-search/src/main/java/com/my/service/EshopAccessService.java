/**
 *
 */
package com.my.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.my.elasticsearch.model.Access;
import com.my.exception.BusinessException;

import com.my.mongo.model.EshopAccess;
import io.searchbox.client.JestResult;
import io.searchbox.core.SearchScroll;
import io.searchbox.params.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author I311862
 */
@Service("eshopAccessService")
public class EshopAccessService {

	@Autowired
	private ElasticSearchManager elasticSearchManager;
	private List<String> notIncludeDomainCN;
	private List<String> notIncludeDomainUS;
	private List<String> notIncludeDomainEU;

	@Autowired
	private MongoDBClient mongoDBClient;


	public EshopAccessService() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		notIncludeDomainCN = new ArrayList<String>();
		notIncludeDomainCN.add("csm.sapanywhere.sap.corp");
		notIncludeDomainCN.add("accounts.sapanywhere.cn");
		notIncludeDomainCN.add("app1.sapanywhere.cn");
		notIncludeDomainCN.add("wechat.sapanywhere.cn");
		notIncludeDomainCN.add("api.sapanywhere.cn");
		notIncludeDomainCN.add("app1.sapanywhere.sap.corp");
		notIncludeDomainCN.add("mp.sapanywhere.cn");
		notIncludeDomainCN.add("dev.sapanywhere.cn");
		notIncludeDomainCN.add("my.sapanywhere.cn");
		notIncludeDomainCN.add("bss.sapanywhere.cn");
		notIncludeDomainCN.add("eap-occ-cn.sapanywhere.cn");
		notIncludeDomainCN.add("idp.sapanywhere.sap.corp:443");
		notIncludeDomainCN.add("channel.sapanywhere.cn");
		notIncludeDomainCN.add("eap-cn.sapanywhere.cn");
		notIncludeDomainCN.add("static.sapanywhere.cn");
		notIncludeDomainCN.add("idp.sapanywhere.sap.corp");
		notIncludeDomainCN.add("doc.sapanywhere.cn");
		notIncludeDomainCN.add("posmy.sapanywhere.cn");
		notIncludeDomainCN.add("eap-idp-cn.sapanywhere.cn");
		notIncludeDomainCN.add("stock.sapanywhere.sap.corp:443");

		notIncludeDomainUS = new ArrayList<String>();
		notIncludeDomainUS.add("bss-us.sapanywhere.com");
		notIncludeDomainUS.add("bss-us.sapanywhere.sap.corp");
		notIncludeDomainUS.add("doc-us.sapanywhere.com");
		notIncludeDomainUS.add("doc-us.sapanywhere.sap.corp");
		notIncludeDomainUS.add("occ1.sapanywhere.com");
		notIncludeDomainUS.add("occ1.sapanywhere.sap.corp");
		notIncludeDomainUS.add("idp.sapanywhere.com");
		notIncludeDomainUS.add("idp.sapanywhere.sap.corp");
		notIncludeDomainUS.add("csm.sapanywhere.com");
		notIncludeDomainUS.add("csm.sapanywhere.sap.corp");
		notIncludeDomainUS.add("doc-us.sapanywhere.com");
		notIncludeDomainUS.add("doc-us.sapanywhere.sap.corp");
		notIncludeDomainUS.add("dev-us.sapanywhere.com");
		notIncludeDomainUS.add("dev-us.sapanywhere.sap.corp");
		notIncludeDomainUS.add("api-us.sapanywhere.com");
		notIncludeDomainUS.add("api-us.sapanywhere.sap.corp");
		notIncludeDomainUS.add("eap-us-mp.sapanywhere.com");
		notIncludeDomainUS.add("eap-us-mp.sapanywhere.sap.corp");
		notIncludeDomainUS.add("poseap-us.sapanywhere.com");
		notIncludeDomainUS.add("poseap-us.sapanywhere.sap.corp");
		notIncludeDomainUS.add("eap-payment-us.sapanywhere.com");
		notIncludeDomainUS.add("eap-payment-us.sapanywhere.sap.corp");
		notIncludeDomainUS.add("eap-occ-us.sapanywhere.com");
		notIncludeDomainUS.add("eap-occ-us.sapanywhere.sap.corp");
		notIncludeDomainUS.add("eap-us.sapanywhere.com");
		notIncludeDomainUS.add("eap-us.sapanywhere.sap.corp");
		notIncludeDomainUS.add("eap-idp-us.sapanywhere.com");
		notIncludeDomainUS.add("eap-idp-us.sapanywhere.sap.corp");
		notIncludeDomainUS.add("app1-us.sapanywhere.com");
		notIncludeDomainUS.add("accounts-us.sapanywhere.com");
		notIncludeDomainUS.add("my-us.sapanywhere.com");
		notIncludeDomainUS.add("mp-us.sapanywhere.com");
		notIncludeDomainUS.add("stock.sapanywhere.sap.corp:443");

	}


	public void searchViews(int dayBefore, String landscape) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
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

		String queryString = "landscape:\\\"" + landscape + "\\\"";

		List<String> notIncludeDomain = null;
		if (landscape.equals("cn")){
			notIncludeDomain = notIncludeDomainCN;
		} else if (landscape.equals("us")) {
			notIncludeDomain = notIncludeDomainUS;
		}

		for(String notDomain : notIncludeDomain){
			queryString += " AND ";
			queryString += "NOT http_host:\\\"" + notDomain +"\\\"";
		}

		String query = elasticSearchManager.buildEshopQueryScollString("desc", queryString, yesterdayBegin,yesterdayEnd);
		System.out.println(query);

		int queryWindow = 5000;
		int currentIndex = 0;
		int totalCount = 0;

		Search search = new Search.Builder(query)
				.setParameter(Parameters.SIZE, queryWindow)
				.setParameter(Parameters.SCROLL, "20m")
				.build();

		SearchResult result = elasticSearchManager.getJestClient().execute(search);

		if (!result.isSucceeded()){
			throw new BusinessException("query elastic search failed:" + result.getErrorMessage());
		}else{
			System.out.println("query success");
			List<Access> articles = result.getSourceAsObjectList(Access.class);
			for (Access access : articles) {
				EshopAccess eshopAccess = new EshopAccess(access);
				eshopAccess.setCreateTime(createTime);
				System.out.println(eshopAccess.toString());
				mongoDBClient.getMongoOperation().save(eshopAccess);
				currentIndex++;
			}

			totalCount = result.getTotal();
		}

		String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
		while (totalCount > currentIndex){
			SearchScroll scroll = new SearchScroll.Builder(scrollId, "20m")
					.setParameter(Parameters.SIZE, queryWindow).build();
			JestResult scollResult = elasticSearchManager.getJestClient().execute(scroll);

			if (!result.isSucceeded()){
				throw new BusinessException("query elastic search failed:" + result.getErrorMessage());
			}else {
				System.out.println("query success");
				List<Access> articles = scollResult.getSourceAsObjectList(Access.class);
				for (Access access : articles) {
					EshopAccess eshopAccess = new EshopAccess(access);
					eshopAccess.setCreateTime(createTime);
					System.out.println(eshopAccess.toString());
					mongoDBClient.getMongoOperation().save(eshopAccess);
					currentIndex++;
				}
			}

			scrollId = scollResult.getJsonObject().getAsJsonPrimitive("_scroll_id").getAsString();
		}







		/*

		int totalCount = 0;
		int from = 0;
		int size = 1000;

		totalCount = queryAndSaveToMongoDB(from, size, dayBefore, landscape);

		while(totalCount > (from + size)){
			from += size;
			queryAndSaveToMongoDB(from, size, dayBefore, landscape);
		}*/

		System.out.println("Finish import " + totalCount + " eshopAccess." );

	}

	public void deleteMongoDBRecord(int dayBefore, String landscape){
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
					new BasicDBObject("createTime",new BasicDBObject("$gte", dateBegin).append("$lt", dateEnd)).
							append("landscape", landscape));
		System.out.println(result);
	}

	public List<EshopAccess> getAccesses(String httpHost, String contentPath, Integer responseTime, Long start, Long end, int status, Integer limit) {
		Date startDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		if (start != null) {
			cal.setTimeInMillis(start.longValue());
			startDate = cal.getTime();
		}
		if (end != null) {
			cal.setTimeInMillis(end.longValue());
			endDate = cal.getTime();
		}

		BasicDBObject queryParams = new BasicDBObject("httpHost", httpHost);
		if (contentPath != null){
			queryParams = queryParams.append("contentPath", contentPath);
		}

		BasicDBObject timeParams;
		if (startDate != null){
			timeParams = new BasicDBObject("$gte", startDate);
		} else {
			Calendar minCal = Calendar.getInstance();
			minCal.set(Calendar.YEAR, 1970);
			timeParams = new BasicDBObject("$gte", minCal.getTime());
		}

		if (endDate != null){
			timeParams = timeParams.append("$lt", endDate);
		}

		queryParams = queryParams.append("time", timeParams);
		queryParams = queryParams.append("status", status);

		if (responseTime != null) {
			queryParams = queryParams.append("responseTime", new BasicDBObject("$gte", responseTime.intValue()));
		}

		if (limit == null){
			limit = new Integer(100);
		}

		DBCursor dbCursor = mongoDBClient.getMongoOperation().getCollection("EshopAccess").
				find(queryParams).
				sort(new BasicDBObject("time", -1)).
				limit(limit.intValue());

		List<EshopAccess> eshopAccesses = new ArrayList<EshopAccess>();
		while (dbCursor.hasNext()){
			DBObject curr = dbCursor.next();
			System.out.println(curr);
			EshopAccess eshopAccess = mongoDBClient.getMongoOperation().getConverter().read(EshopAccess.class, curr);
			eshopAccesses.add(eshopAccess);
		}

		return eshopAccesses;
	}
}
