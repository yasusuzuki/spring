package demo;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Goto this Spring demo video
 * https://www.youtube.com/watch?v=GTrNkhVnJBU
 *
 */
@Controller
@EnableAutoConfiguration
public class MySampleController {
	Logger logger = Logger.getLogger(MySampleController.class.getName());
	@RequestMapping("/my_sample")
	@ResponseBody
	String my_sample(){
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.ALL);
		logger.setLevel(Level.ALL);
		logger.info("Log Level=" + logger.getLevel());
		logger.fine("FINE");
		return "My aabbcc   !!";
	}
	
}
