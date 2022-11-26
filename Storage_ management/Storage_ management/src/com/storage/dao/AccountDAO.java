package com.storage.dao;

import com.storage.entity.AccountE;
import com.storage.utils.JDBC;
import com.storage.utils.JDBC;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MyPC
 */
public class AccountDAO extends StorageDAO<AccountE, String> {

    final String INSERT_SQL = "INSERT INTO LOGIN (USERNAME, FULLNAME, PASSWORD, PHONE, EMAIL, POSITION) VALUES(?, ?, ?, ?, ?)";
    final String UPDATE_SQL = "UPDATE LOGIN SET FULLNAME = ?, PASSWORD = ?, PHONE = ?,EMAIL = ?, POSITION = ? WHERE USERNAME = ?";
    final String DELETE_SQL = "DELETE FROM LOGIN WHERE USERNAME = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM LOGIN";
    final String SELECT_BY_ID_SQL = "SELECT * FROM LOGIN WHERE USERNAME LIKE ?";

    @Override
    public void insert(AccountE entity) {
        JDBC.update(INSERT_SQL, entity.getUserName(), entity.getFullName(), entity.getPassWord(), entity.getPhone(), entity.getEmail(), entity.isPosition());
    }

    @Override
    public void update(AccountE entity) {
        JDBC.update(UPDATE_SQL, entity.getFullName(), entity.getPassWord(), entity.getPhone(), entity.getEmail(), entity.isPosition(), entity.getUserName());
    }

    @Override
    public void delete(String key) {
        JDBC.update(DELETE_SQL, key);
    }

    @Override
    public List<AccountE> selectAll() {
        return this.selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public AccountE selectById(String key) {
        List<AccountE> list = selectBySql(SELECT_BY_ID_SQL, key);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<AccountE> selectBySql(String sql, Object... args) {
        List<AccountE> list = new ArrayList<AccountE>();
        try {
            ResultSet rss = JDBC.query(sql, args);
            while (rss.next()) {
                AccountE entity = new AccountE();
                entity.setUserName(rss.getString("USERNAME"));
                entity.setFullName(rss.getString("FULLNAME"));
                entity.setPassWord(rss.getString("PASSWORD"));
                entity.setPhone(rss.getString("PHONE"));
                entity.setEmail(rss.getString("EMAIL"));
                entity.setPosition(rss.getBoolean("POSITION"));
                list.add(entity);
            }
            rss.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
