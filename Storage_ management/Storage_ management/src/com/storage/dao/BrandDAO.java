package com.storage.dao;

import com.storage.entity.BrandE;
import com.storage.entity.TypeE;
import com.storage.utils.JDBC;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

/**
 *
 * @author MyPC
 */
public class BrandDAO extends StorageDAO<BrandE, String> {

    final String SELECT_ALL_SQL = "SELECT * FROM BRAND";
    final String SELECT_BY_ID_SQL = "SELECT * FROM BRAND WHERE IDBRAND = ?";

    @Override
    public void insert(BrandE entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(BrandE entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<BrandE> selectAll() {
        return this.selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public BrandE selectById(String key) {
        List<BrandE> list = this.selectBySql(SELECT_BY_ID_SQL, key);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<BrandE> selectBySql(String sql, Object... args) {
        List<BrandE> list = new ArrayList<BrandE>();
        try {
            ResultSet rs = JDBC.query(sql, args);
            while (rs.next()) {
                BrandE entity = new BrandE();
                entity.setIdBrand(rs.getString("IDBRAND"));
                entity.setBrand(rs.getString("BRAND"));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
