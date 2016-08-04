package org.opensextant.tagger.solr;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.CoreContainer;
import org.opensextant.tagger.Document;
import org.opensextant.tagger.Match;
import org.opensextant.tagger.Tagger;
import org.opensextant.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrTagger implements Tagger {

	/** Log object. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SolrTagger.class);

	private static EmbeddedSolrServer solrClient;

	private SolrTaggerRequest tagRequest;
	private Map<Integer, Map<String, Object>> idMap = new HashMap<Integer, Map<String, Object>>(100);

	public ModifiableSolrParams matchParams = new ModifiableSolrParams();
	public ModifiableSolrParams searchParams = new ModifiableSolrParams();

	/** The matching request handler. */
	private static final String MATCH_REQUESTHANDLER = "/tag";
	private static final String SEARCH_FIELD = "name";

	public SolrTagger(String solrHome, String coreName, String matchField) {

		if (solrHome == null || solrHome.isEmpty()) {
			LOGGER.info("No value given for Solr home. Trying system property");
			solrHome = System.getProperty("solr.home");

			if (solrHome == null || solrHome.isEmpty()) {
				LOGGER.error("No value given for Solr home and no system property");
				return;
			}
		}

		LOGGER.info("Using Solr home = " + solrHome);

		if (solrClient == null) {
			CoreContainer solrContainer = new CoreContainer(solrHome);
			solrContainer.load();
			solrClient = new EmbeddedSolrServer(solrContainer, coreName);
		}

		matchParams.set(CommonParams.QT, MATCH_REQUESTHANDLER);
		matchParams.set(CommonParams.FL, "*");
		matchParams.set("tagsLimit", 100000);
		matchParams.set(CommonParams.ROWS, 100000);
		matchParams.set("subTags", false);
		matchParams.set("matchText", false);
		matchParams.set("overlaps", "LONGEST_DOMINANT_RIGHT");
		matchParams.set("field", matchField);

		searchParams.set(CommonParams.Q, "*:*");
		searchParams.set(CommonParams.FL, "* score");
		searchParams.set(CommonParams.DF, SEARCH_FIELD);
		searchParams.set(CommonParams.ROWS, 100000);

	}

	public void setCoreName(String name, String matchField) {
		matchParams.set(CommonParams.FL, Utils.getFieldNames(solrClient, name));
		matchParams.set("field", matchField);
	}

	public Document tag(String content) {

		Document doc = new Document();
		doc.setContent(content);

		doc.setMatchList(this.match(content));

		return doc;
	}

	@Override
	public Document tag(File content) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document tag(URL content) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Match> match(String content) {

		List<Match> matches = new ArrayList<Match>();
		// Setup request to tag
		tagRequest = new SolrTaggerRequest(matchParams, SolrRequest.METHOD.POST);
		tagRequest.setInput(content);

		QueryResponse response = null;

		try {
			response = tagRequest.process(solrClient);
		} catch (SolrServerException | IOException e) {
			LOGGER.error("Got exception when attempting to match ", e);
			return matches;
		}

		// Process Solr Response
		SolrDocumentList docList = response.getResults();

		// TODO convert this section to use a StreamingResponseCallback

		// convert each solrdoc (a match) to a feature and add to id map
		for (SolrDocument solrDoc : docList) {
			Integer id = (Integer) solrDoc.getFirstValue("id");
			Map<String, Object> fields = solrDoc.getFieldValueMap();
			idMap.put(id, fields);
		}

		@SuppressWarnings("unchecked")
		List<NamedList<?>> tags = (List<NamedList<?>>) response.getResponse().get("tags");

		for (NamedList<?> tag : tags) {
			Match match = new Match();
			int x1 = -1, x2 = -1;

			// get the start, end and list of matching place IDs
			x1 = (Integer) tag.get("startOffset");
			x2 = (Integer) tag.get("endOffset");

			@SuppressWarnings("unchecked")
			List<Integer> idList = (List<Integer>) tag.get("ids");

			// populate the Match
			match.setStart(x1);
			match.setEnd(x2);
			match.setMatchText(content.substring(x1, x2));
			for (Integer id : idList) {
				Map<String, Object> fields = idMap.get(id);
				match.addPayload(fields);
			}
			matches.add(match);
		}

		return matches;

	}

	@Override
	public List<Match> match(File content) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Match> match(URL content) {
		// TODO Auto-generated method stub
		return null;
	}

	public void cleanup() {
		try {
			SolrTagger.solrClient.close();
		} catch (IOException e) {
			LOGGER.error("Error closing Solr Matcher:", e);
		}
	}

}
