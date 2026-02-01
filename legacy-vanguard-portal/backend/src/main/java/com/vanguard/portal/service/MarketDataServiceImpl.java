package com.vanguard.portal.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for retrieving real-time market data and stock quotes.
 * Integrates with legacy SOAP-based market data provider.
 *
 * LEGACY ISSUE: Uses SOAP web services instead of modern REST APIs
 *
 * @author Legacy Team
 * @since 2012
 */
@Service("marketDataService")
public class MarketDataServiceImpl implements MarketDataService {

    private static final Logger logger = Logger.getLogger(MarketDataServiceImpl.class);

    // Legacy SOAP endpoint from 2012 - still in production!
    private static final String SOAP_ENDPOINT = "http://marketdata.legacyprovider.com/soap/v1/quotes";
    private static final String SOAP_NAMESPACE = "http://schemas.legacyprovider.com/marketdata/2012";

    /**
     * LEGACY TECHNOLOGY: SOAP Web Service Client
     *
     * This service uses SOAP (Simple Object Access Protocol), a legacy technology
     * that has been largely replaced by REST APIs in modern applications.
     *
     * PROBLEMS WITH THIS APPROACH:
     * 1. Complexity: SOAP requires XML parsing, WSDL files, and verbose message structure
     * 2. Performance: XML serialization/deserialization is slower than JSON
     * 3. Overhead: SOAP messages are 3-5x larger than equivalent REST/JSON
     * 4. Maintenance: Hard to debug, limited tooling support, steep learning curve
     * 5. Brittleness: Tight coupling to WSDL schema, difficult to version
     * 6. No caching: SOAP POST requests can't leverage HTTP caching
     *
     * IMPACT:
     * - Quote retrieval takes 500-800ms vs 50-100ms for REST equivalent
     * - High network bandwidth usage (300KB vs 5KB for same data)
     * - Frequent timeouts during market volatility
     * - New developers struggle to understand and maintain this code
     *
     * RECOMMENDED FIX:
     * - Migrate to modern REST API with JSON (e.g., Alpha Vantage, IEX Cloud)
     * - Implement caching layer (Redis) to reduce API calls
     * - Use HTTP/2 for connection pooling and multiplexing
     * - Consider WebSocket for real-time streaming quotes
     *
     * BUSINESS CONTEXT:
     * The market data provider has offered a REST API since 2018, but migration
     * was deprioritized due to "if it ain't broke, don't fix it" mentality.
     * Monthly SOAP API costs are 3x higher than REST equivalent.
     */
    @Override
    public Map<String, BigDecimal> getStockQuotes(String[] symbols) {
        logger.info("Fetching quotes for " + symbols.length + " symbols via SOAP");

        Map<String, BigDecimal> quotes = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            // Create SOAP connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Build SOAP message
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();

            // SOAP Envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("md", SOAP_NAMESPACE);

            // SOAP Body with quote request
            SOAPBody soapBody = envelope.getBody();
            SOAPElement getQuotes = soapBody.addChildElement("GetQuotes", "md");

            for (String symbol : symbols) {
                SOAPElement symbolElement = getQuotes.addChildElement("Symbol", "md");
                symbolElement.addTextNode(symbol);
            }

            // Add authentication header (legacy credentials in plain text!)
            SOAPHeader soapHeader = envelope.getHeader();
            SOAPElement authHeader = soapHeader.addChildElement("Authentication", "md");
            SOAPElement username = authHeader.addChildElement("Username", "md");
            username.addTextNode("vanguard_prod"); // Hardcoded credentials - another issue!
            SOAPElement password = authHeader.addChildElement("Password", "md");
            password.addTextNode("legacy2012"); // Security risk!

            soapMessage.saveChanges();

            // Log the SOAP request (verbose XML)
            if (logger.isDebugEnabled()) {
                logger.debug("SOAP Request: " + soapMessage.toString());
            }

            // Make SOAP call to endpoint
            URL endpoint = new URL(SOAP_ENDPOINT);
            SOAPMessage soapResponse = soapConnection.call(soapMessage, endpoint);

            // Parse SOAP response
            SOAPBody responseBody = soapResponse.getSOAPBody();

            // Check for SOAP faults
            if (responseBody.hasFault()) {
                SOAPFault fault = responseBody.getFault();
                logger.error("SOAP Fault: " + fault.getFaultString());
                return quotes; // Return empty map on error
            }

            // Extract quote data from XML response
            // This is incredibly verbose compared to parsing JSON
            org.w3c.dom.NodeList quoteNodes = responseBody.getElementsByTagNameNS(SOAP_NAMESPACE, "Quote");

            for (int i = 0; i < quoteNodes.getLength(); i++) {
                org.w3c.dom.Node quoteNode = quoteNodes.item(i);
                if (quoteNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    org.w3c.dom.Element quoteElement = (org.w3c.dom.Element) quoteNode;

                    String symbol = quoteElement.getElementsByTagNameNS(SOAP_NAMESPACE, "Symbol")
                            .item(0).getTextContent();
                    String priceStr = quoteElement.getElementsByTagNameNS(SOAP_NAMESPACE, "LastPrice")
                            .item(0).getTextContent();

                    BigDecimal price = new BigDecimal(priceStr);
                    quotes.put(symbol, price);
                }
            }

            soapConnection.close();

            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("Retrieved " + quotes.size() + " quotes via SOAP in " + elapsedTime + "ms");

            // Log performance issues
            if (elapsedTime > 1000) {
                logger.warn("SLOW SOAP CALL: Quote retrieval took " + elapsedTime + "ms for " + symbols.length + " symbols");
            }

        } catch (Exception e) {
            logger.error("Error calling SOAP market data service", e);
            // Fallback to cached prices or default values
            for (String symbol : symbols) {
                quotes.put(symbol, BigDecimal.ZERO); // Return zero on error
            }
        }

        return quotes;
    }

    /**
     * Gets a single stock quote using the SOAP service.
     * Convenience method that wraps the batch quote method.
     */
    @Override
    public BigDecimal getStockQuote(String symbol) {
        logger.info("Fetching single quote for symbol: " + symbol);

        String[] symbols = {symbol};
        Map<String, BigDecimal> quotes = getStockQuotes(symbols);

        return quotes.getOrDefault(symbol, BigDecimal.ZERO);
    }

    /**
     * Retrieves market indices (DOW, S&P 500, NASDAQ).
     * Also uses the legacy SOAP service.
     */
    @Override
    public Map<String, BigDecimal> getMarketIndices() {
        logger.info("Fetching market indices via SOAP");

        String[] indices = {"^DJI", "^GSPC", "^IXIC"};
        return getStockQuotes(indices);
    }
}
