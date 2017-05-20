package demo.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.springframework.stereotype.Component;


/**
 * @author Trifon Trifonov
 */
@Component
public class MwsProductRoute extends RouteBuilder {

	@Override
	public void configure() {
		// VERY IMPORTANT!!!
		//                   <ListMatchingProductsResponse xmlns="http://mws.amazonservices.com/schema/Products/2011-10-01">
		Namespaces productsNamespace = new Namespaces("mws-prd", "http://mws.amazonservices.com/schema/Products/2011-10-01");

		from("servlet:///mws/product?matchOnUriPrefix=true")
			.setHeader("mwsSearchString").simple("${in.header.searchString}")
			.setHeader("mwsSearchContext").simple("${in.header.searchContext}")

//			.enrich().simple("vm:direct-mws-product-query?bridgeEndpoint=true")
			.enrich().simple("vm:direct-mws-product-query") // We can add dynamic values read from the incoming message or from Headers!

			// BEGIN - split
			// Both xpath expressions WORK!!!
//			.split(productsNamespace.xpath("//mws-prd:ListMatchingProductsResponse/mws-prd:ListMatchingProductsResult/mws-prd:Products/mws-prd:Product"), new StringAggregationStrategy())
			.split(productsNamespace.xpath("//mws-prd:Products/mws-prd:Product"), new StringAggregationStrategy())

//				.aggregate(new StringAggregationStrategy()) // Collect the parts and reassemble
//					.header("MwsRequestId") // MwsRequestId tells us which parts belong together
//					.completionTimeout( 500L ) // wait for 0.5 seconds to aggregate

				.setHeader("asin", productsNamespace.xpath("//mws-prd:Identifiers/mws-prd:MarketplaceASIN/mws-prd:ASIN", java.lang.String.class))

				.to("log:servlet-mws-product-element-split?showHeaders=true")

				// This is valid only if ExchangeAggregator is used!!!
				// Set body to empty, because otherwise the aggregate will
				// begin with the original XML
//				.setBody(constant(""))

			// The end() here marks the end of the split-aggregate operation.
			// Beyond this point, we are working on the aggregated result.
			.end()
			// END - split

//			.setBody(simple("Search results for '<b>${in.header.searchString}</b>':<p/>\n${body}"))
			.setBody(simple("<result>${body}</result>"))

			.to("log:servlet-mws-product?showHeaders=true")
		;

		// Main route which is throttled
		from("vm:direct-mws-product-query").routeId("route.direct-mws-product-query")
			.throttle(3).rejectExecution(true).timePeriodMillis(60 * 60 * 1000) // TODO - proper error handler when message is throttled
			.to("mws-product://DE?mwsUrl=RAW({{MWS_URL}})&merchantId=RAW({{MWS_MERCHANT_ID}})&accessKey=RAW({{MWS_ACCESS_KEY}})&secretAccessKey=RAW({{MWS_SECRET_ACCESS_KEY}})")
		;
	}

}