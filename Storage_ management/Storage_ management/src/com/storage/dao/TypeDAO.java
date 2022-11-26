package com.storage.dao;

import com.storage.entity.TypeE;
import com.storage.utils.JDBC;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MyPC
 */
public class TypeDAO extends StorageDAO<TypeE, String> {

    final String SELECT_ALL_SQL = "SELECT * FROM TYPE";
    final String SELECT_BY_ID_SQL = "SELECT * FROM TYPE WHERE IDTYPE = ?";

    @Override
    public void insert(TypeE entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(TypeE entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<TypeE> selectAll() {
        return this.selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public TypeE selectById(String key) {
        List<TypeE> list = this.selectBySql(SELECT_BY_ID_SQL, key);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<TypeE> selectBySql(String sql, Object... args) {
        List<TypeE> list = new ArrayList<TypeE>();
        try {
            ResultSet rs = JDBC.query(sql, args);
            while (rs.next()) {
                TypeE entity = new TypeE();
                entity.setIdType(rs.getString("IDTYPE"));
                entity.setType(rs.getString("TYPE"));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
