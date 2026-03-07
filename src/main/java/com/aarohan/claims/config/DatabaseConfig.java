import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {
    private static DataSource instance; 
    private DatabaseConfig () { 

    }
    public static DataSource getDataSource() { 
        if (instance == null) { 
            instance = initializeDataSource();
        } 
        return instance;
    }
    
    private static DataSource initializeDataSource() {
        HikariConfig config = new HikariConfig(); 
        config.setJdbcUrl("jdbc:postgresql://127.0.0.1:5432/claims_db"); 
        config.setUsername("aarohan");
        config.setPassword(""); 
        config.setMaximumPoolSize(10); 
        HikariDataSource dataSource = new HikariDataSource(config); 
        return dataSource;
    }
    
}
