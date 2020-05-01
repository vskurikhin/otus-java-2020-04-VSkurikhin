package su.svn.hiload.socialnetwork.dao.jdbc.mappers;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import su.svn.hiload.socialnetwork.model.UserInterest;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserInterestBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    private final List<UserInterest> batchList;

    public UserInterestBatchPreparedStatementSetter(List<UserInterest> batchList) {
        this.batchList = batchList;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        UserInterest employee = batchList.get(i);
        ps.setLong(1, employee.getUserInfoId());
        ps.setString(2, employee.getInterest());
    }

    @Override
    public int getBatchSize() {
        return batchList.size();
    }
}
