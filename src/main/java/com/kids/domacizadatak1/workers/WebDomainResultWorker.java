package com.kids.domacizadatak1.workers;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class WebDomainResultWorker implements Callable<Map<String, Map<String, Integer>>> {

    private Map<String, Future<Map<String, Integer>>> webJobResultsMap;
    private Map<String, Future<Map<String, Integer>>> webDomainResultsMap;

    public WebDomainResultWorker(Map<String, Future<Map<String, Integer>>> webJobResultsMap,
                                    Map<String, Future<Map<String, Integer>>> webDomainResultsMap) {
        this.webJobResultsMap = webJobResultsMap;
        this.webDomainResultsMap = webDomainResultsMap;
    }

    @Override
    public Map<String, Map<String, Integer>> call() throws Exception {
        return null;
    }

    /*
        else if (fileWebToken.equals("web")) {

			Map<String, Map<String, Integer>> resultMap = new ConcurrentHashMap<>();

			if (domainWebResults.get(pathToken) != null) {
				resultMap.put(pathToken, domainWebResults.get(pathToken));
				if (queryGetToken.equals("query")) {
					System.out.println(resultMap);
				}
				return resultMap;
			}

			Map<String, Integer> domainSumMap = new ConcurrentHashMap<>();

			boolean domainExists = false;
			for (String key : webPageResults.keySet()) {
				try {
					URL url = new URL(key);
					String domain = url.getHost();
					if (pathToken.equals(domain)) {
						domainExists = true;
						if (queryGetToken.equals("query")) {
							if (! webPageResults.get(key).isDone()) {
								System.out.println(domain + " isn't done...");
								return null;
							}
						}

						domainSumMap = Stream.concat(domainSumMap.entrySet().stream(), webPageResults.get(key).get().entrySet().stream())
									.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));

					}
				} catch (MalformedURLException | InterruptedException | ExecutionException e) {
					System.err.println(key + " {" + e.getMessage() +"}");
				}
			}

			if (! domainExists) {
				System.out.println("Domain {" + pathToken + "} doesn't exist.");
				return null;
			}

			domainWebResults.put(pathToken, domainSumMap);


			resultMap.put(pathToken, domainSumMap);

			if (queryGetToken.equals("query")) {
				System.out.println(resultMap);
			}

			return resultMap;

		}
        */
}
