package demo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
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
	Long id = 100L;
    @Autowired
    private JdbcTemplate jdbc;
    
	@RequestMapping("/get")
	@ResponseBody
	public String my_sample(){
		List<String> ids = this.jdbc.queryForList("select title from TODOS",String.class);
		logger.info("my_sample " + ids);
		return "GET: TODOS " + ids.toString();
	}
	
	@RequestMapping("/add")
	@ResponseBody
	public String add(){
		this.jdbc.update("INSERT INTO TODOS (id,status,title,due_date,priority,description,done_date) VALUES (?,?,?,?,?,?,?)",id,"OPEN","title","2019-12-31","AAA","desc",null);
		id++;
		logger.info("add");
		return "ADD 100";
	}
}
