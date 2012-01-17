package pl.rafalmag.howlibwork;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jTest {

	@Test
	public void testName() throws Exception {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		logger.trace("trace");
		logger.debug("debug");
		logger.info("info");
		logger.warn("warn");
		logger.error("error");
	}

}
