package com.kids.domacizadatak1.workers;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class SummaryResultWorker implements Callable<Map<String, Map<String, Integer>>> {

    private String summaryType;
    private Map<String, Future<Map<String, Integer>>> fileJobResultsMap;
    private Map<String, Future<Map<String, Integer>>> webJobResultsMap;
    private Map<String, Future<Map<String, Integer>>> webDomainResultsMap;
    private Map<String, Map<String, Integer>> fileSummaryResultsMap;
    private Map<String, Map<String, Integer>> webSummaryResultsMap;

    public SummaryResultWorker(String summaryType,
                               Map<String, Future<Map<String, Integer>>> fileJobResultsMap,
                               Map<String, Future<Map<String, Integer>>> webJobResultsMap,
                               Map<String, Future<Map<String, Integer>>> webDomainResultsMap,
                               Map<String, Map<String, Integer>> fileSummaryResultsMap,
                               Map<String, Map<String, Integer>> webSummaryResultsMap) {
        this.summaryType = summaryType;
        this.fileJobResultsMap = fileJobResultsMap;
        this.webJobResultsMap = webJobResultsMap;
        this.webDomainResultsMap = webDomainResultsMap;
        this.fileSummaryResultsMap = fileSummaryResultsMap;
        this.webSummaryResultsMap = webSummaryResultsMap;
    }

    @Override
    public Map<String, Map<String, Integer>> call() throws Exception {
        /*
        if (query.getTokens().size() != 2) {
			Util.errorMessage("summaryResult() dobio pogresan query. {" + Arrays.asList(query.getTokens()) + "}");
			return null;
		}

		String queryGetToken = query.getTokens().get(0);
		String fileWebToken = query.getTokens().get(1);

		if (fileWebToken.equals("file")) {
			if (! summaryCorpusResults.isEmpty()) {
				if (queryGetToken.equals("query")) {
					summaryCorpusResults.forEach((k, v) -> System.out.println((k + ":" + v)));
				}
				return summaryCorpusResults;
			}
			else {
				if (queryGetToken.equals("query")) {
					for (String key : corpusResults.keySet()) {
						if (! corpusResults.get(key).isDone()) {
							System.out.println("File Summary not ready.");
							return null;
						}
					}
				}
				for (String key : corpusResults.keySet()) {
					try {
						summaryCorpusResults.put(key, corpusResults.get(key).get());
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				}
				if (queryGetToken.equals("query")) {
					summaryCorpusResults.forEach((k, v) -> System.out.println((k + ":" + v)));
				}
				return summaryCorpusResults;
			}

		}

		else if (fileWebToken.equals("web")) {

			if (! summaryWebResults.isEmpty()) {
				if (queryGetToken.equals("query")) {
					summaryWebResults.forEach((k, v) -> System.out.println(k + " : " + v));
				}
				return summaryWebResults;
			}

			for (String key : webPageResults.keySet()) {
				if (queryGetToken.equals("query")) {
					if (! webPageResults.get(key).isDone()) {
						System.out.println(key + " isn't done...");
						return null;
					}
				}
			}

			for (String key : webPageResults.keySet()) {
				try {
					URL url;
					url = new URL(key);
					String domain = url.getHost();
					if (summaryWebResults.get(domain) == null) {
						summaryWebResults.put(domain, webPageResults.get(key).get());
					}
					else {
						Map<String, Integer> domainMap = summaryWebResults.get(domain);
						domainMap = Stream.concat(domainMap.entrySet().stream(), webPageResults.get(key).get().entrySet().stream())
								.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));
						summaryWebResults.put(domain, domainMap);
					}
				}
				catch (MalformedURLException | InterruptedException | ExecutionException e) {
					System.err.println(key + " {" + e.getMessage() +"}");
				}
			}

			if (queryGetToken.equals("query")) {
				summaryWebResults.forEach((k, v) -> System.out.println(k + " : " + v));
			}

			return summaryWebResults;
		}

		return null;
        */
        return null;
    }
}
