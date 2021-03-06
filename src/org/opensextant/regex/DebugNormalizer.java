package org.opensextant.regex;

import java.util.Map;
import java.util.regex.MatchResult;

public class DebugNormalizer implements Normalizer {

	/**
	 * Log object private static final Logger LOGGER =
	 * LoggerFactory.getLogger(DebugNormalizer.class);
	 */
	@Override
	public void normalize(RegexAnnotation anno, RegexRule r, MatchResult matchResult) {

		Map<String, Object> annoFeatures = anno.getFeatures();

		int numGroups = matchResult.groupCount();

		for (int i = 0; i < numGroups + 1; i++) {
			// Future: create sub-annotations?

			String elemenValue = matchResult.group(i);
			String elemName = r.getElementMap().get(i);
			annoFeatures.put(elemName, elemenValue);
		}

		annoFeatures.put("entityType", r.getEntityType());
		annoFeatures.put("ruleFamily", r.getRuleFamily());
		annoFeatures.put("ruleName", r.getRuleName());
	}
}
