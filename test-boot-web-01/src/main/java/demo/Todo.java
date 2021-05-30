package demo;
import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

import lombok.Data;

@Data
@Entity
@Table(name="TODOS")

public class Todo {
	/*CREATE TABLE IF NOT EXISTS TODOS (
  id int NOT NULL,
  status CHAR(4) NOT NULL,
  title VARCHAR(128) NOT NULL,
  due_date DATE NOT NULL,
  priority VARCHAR(12) NULL,
  description VARCHAR(1024) NULL,
  done_date DATE NULL
);
*/
	@Id
	@GeneratedValue
	private long id;
 
	private String status;
	private String title;
	private Date due_date;
	private String priority;
	private String description;
	private Date done_date;
}
