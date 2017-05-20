package demo.route;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Trifon Trifonov
 */
public class StringAggregationStrategy implements AggregationStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(StringAggregationStrategy.class);

	private static final String DELIMITER = "\r\n";

	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		LOG.debug("OldExchange: %s; NewExchange: %s", oldExchange, newExchange );
		if (oldExchange == null) {
			return newExchange;
		}

		String oldBody = oldExchange.getIn().getBody(String.class);
		String newBody = newExchange.getIn().getBody(String.class);
		LOG.debug("OldExchange.body: %s; NewExchange.body: %s", oldExchange.getIn().getBody(), newExchange.getIn().getBody() );

		oldExchange.getIn().setBody(oldBody + DELIMITER + newBody);

//		oldExchange.getOut().setBody(oldBody + DELIMITER + newBody);
		return oldExchange;
	}
}