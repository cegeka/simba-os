package org.simbasecurity.refimpl.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LargeTestDataset {

    private final PlatformTransactionManager txManager;
    private final JdbcTemplate jdbcTemplate;

    private boolean largeDatasetEnabled = false;
    private int largeDatasetAmount = 10;

    @Autowired
    public LargeTestDataset(PlatformTransactionManager txManager,
                            JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.txManager = txManager;
    }

    @Value("${simba.large-test-dataset.enabled}")
    public void setLargeDatasetEnabled(boolean largeDatasetEnabled) {
        this.largeDatasetEnabled = largeDatasetEnabled;
    }

    @Value("${simba.large-test-dataset.amount}")
    public void setLargeDatasetAmount(int largeDatasetAmount) {
        this.largeDatasetAmount = largeDatasetAmount;
    }

    private static final String sqlString =
            "insert into SIMBA_USER (ID, VERSION,CHANGEPASSWORDONNEXTLOGON,DATEOFLASTPASSWORDCHANGE,FIRSTNAME,INACTIVEDATE,INVALIDLOGINCOUNT,LANGUAGE,NAME,PASSWORD,PASSWORDCHANGEREQUIRED,STATUS,SUCCESSURL,USERNAME,DATABASELOGINBLOCKED) "
            + "values (?,1,0,NOW,?,null,0,'nl_NL',null,'TqXiRtMAW6CRV/qCs2e6fUV4tRahHTTnjFzwfA==',0,'ACTIVE',null,?,0);";

    @EventListener
    @Transactional
    public void handleContextRefresh(ContextRefreshedEvent event) {
        if (largeDatasetEnabled && event.getApplicationContext().getParent() == null) {
            for (int i = 1; i <= largeDatasetAmount; i++) {
                String userName = String.format("testuser%05d", i);
                jdbcTemplate.update(sqlString, i + 10000, userName, userName);
            }
        }
    }

}
