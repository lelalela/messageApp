package app.messages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageRepository {
	
	private final static Log log = LogFactory.getLog(MessageRepository.class);
	
	private SessionFactory sessionFactory;
	
	public MessageRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Message saveMessage(Message message) {
		Session session = sessionFactory.openSession();
		session.save(message);
		return message;
	}
	
	
//	private NamedParameterJdbcTemplate jdbcTemplate;
	
//	@Autowired
//	public void setDataSource(DataSource dataSource) {
//		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//	}
	
//	private DataSource dataSource;
	
//	public MessageRepository(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}
	

//	public Message saveMessage(Message message) {
//		Connection c = DataSourceUtils.getConnection(dataSource);
//		try {
//			String sql = "INSERT INTO messages (id, text, create_date) VALUES (null, ?, ?)";
//			PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//			ps.setString(1, message.getText());
//			ps.setTimestamp(2, new Timestamp(message.getCreateDate().getTime()));
//			int rowAffected = ps.executeUpdate();
//			
//			if(rowAffected > 0) {
//				ResultSet result = ps.getGeneratedKeys();
//				if(result.next()) {
//					int id = result.getInt(1);
//					return new Message(id, message.getText(), message.getCreateDate());
//				}else {
//					log.error("Failed to retriebe id, No row in result set");
//					return null;
//				}
//			}else {
//				return null;
//			}
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//			log.error("Failed to save message", e);
//			try {
//				c.close();
//			} catch (SQLException ex) {
//				ex.printStackTrace();
//				log.error("Failed to Connection", ex);
//			}
//		} finally {
//			DataSourceUtils.releaseConnection(c, dataSource);
//		}
//		return null;
//	}
	
//	public Message saveMessage(Message message) {
//		GeneratedKeyHolder holder = new GeneratedKeyHolder();
//		MapSqlParameterSource param = new MapSqlParameterSource();
//		param.addValue("text", message.getText());
//		param.addValue("createDate", message.getCreateDate());
//		String sql = "INSERT INTO messages (id, text, create_date) VALUES (null, :text, :createDate)";
//		try {
//			this.jdbcTemplate.update(sql, param, holder);
//		} catch (DataAccessException e) {
//			log.error("Failed to save message" , e);
//			return null;
//		}
//		return new Message(holder.getKey().intValue(), message.getText(), message.getCreateDate());
//	}
}