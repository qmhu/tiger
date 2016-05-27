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
import com.my.mongo.model.OCCAccess;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchScroll;
import io.searchbox.params.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author I311862
 */
@Service("occAccessService")
public class OCCAccessService {

	@Autowired
	private ElasticSearchManager elasticSearchManager;

	@Autowired
	private MongoDBClient mongoDBClient;

	public void pullOCCAccess(int dayBefore) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
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

		String query = elasticSearchManager.buildOCCQueryScollString("desc", "name:\\\"WordPress\\\" AND NOT request_uri:*wp\\\\-*",yesterdayBegin,yesterdayEnd);
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
			System.out.println("query occ success");
			List<Access> articles = result.getSourceAsObjectList(Access.class);
			for (Access access : articles) {
				OCCAccess occAccess = new OCCAccess(access);
				occAccess.setCreateTime(createTime);
				System.out.println(occAccess.toString());
				mongoDBClient.getMongoOperation().save(occAccess);
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
				System.out.println("query occ success");
				List<Access> articles = scollResult.getSourceAsObjectList(Access.class);
				for (Access access : articles) {
					OCCAccess occAccess = new OCCAccess(access);
					occAccess.setCreateTime(createTime);
					System.out.println(occAccess.toString());
					mongoDBClient.getMongoOperation().save(occAccess);
					currentIndex++;
				}
			}

			scrollId = scollResult.getJsonObject().getAsJsonPrimitive("_scroll_id").getAsString();
		}

		System.out.println("Finish import " + totalCount + " occAccess." );

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
				getCollection("OCCAccess").remove(
					new BasicDBObject("createTime",new BasicDBObject("$gte", dateBegin).append("$lt", dateEnd)));
		System.out.println(result);
	}

	public List<OCCAccess> getOCCAccesses(String contentPath, Long start, Long end, int status, Integer limit) {
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

		BasicDBObject queryParams = new BasicDBObject("contentPath", contentPath);
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
		if (limit == null){
			limit = new Integer(100);
		}

		DBCursor dbCursor = mongoDBClient.getMongoOperation().getCollection("OCCAccess").
				find(queryParams).
				sort(new BasicDBObject("time", -1)).
				limit(limit.intValue());

		List<OCCAccess> occAccesses = new ArrayList<OCCAccess>();
		while (dbCursor.hasNext()){
			DBObject curr = dbCursor.next();
			System.out.println(curr);
			OCCAccess occAccess = mongoDBClient.getMongoOperation().getConverter().read(OCCAccess.class, curr);
			occAccesses.add(occAccess);
		}

		return occAccesses;
	}

}
